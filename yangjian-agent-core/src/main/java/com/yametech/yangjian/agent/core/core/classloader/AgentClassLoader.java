/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yametech.yangjian.agent.core.core.classloader;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.AgentPath;
import com.yametech.yangjian.agent.core.util.Util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The <code>AgentClassLoader</code> represents a classloader,
 * which is in charge of finding plugins and interceptors.
 *
 * @author wusheng
 */
public class AgentClassLoader extends ClassLoader {
    private static final ILogger log = LoggerFactory.getLogger(AgentClassLoader.class);
    private static AgentClassLoader DEFAULT_LOADER;
    private static Map<ClassLoader, AgentClassLoader> CLASS_LOADERS = new ConcurrentHashMap<>();
    private static final String EXTEND_PLUGIN_DEFAULT_DIR = "/data/www/soft/agent-custom";
    private static List<File> classpath = new LinkedList<>();
    private static List<Jar> allJars = new LinkedList<>();

    static {
        tryRegisterAsParallelCapable();
        initClasspath();
        initJar();
    }

    /**
     * Functional Description: solve the classloader dead lock when jvm start
     * only support JDK7+, since ParallelCapable appears in JDK7+
     */
    private static void tryRegisterAsParallelCapable() {
        Method[] methods = ClassLoader.class.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if ("registerAsParallelCapable".equalsIgnoreCase(methodName)) {
                try {
                    method.setAccessible(true);
                    method.invoke(null);
                } catch (Exception e) {
                    log.warn(e, "can not invoke ClassLoader.registerAsParallelCapable()");
                }
                return;
            }
        }
    }

    /**
     * 初始化jar包加载路径
     */
    private static void initClasspath() {
        File agentDictionary = AgentPath.getCompatiblePath();
//        System.err.println(agentDictionary.getAbsolutePath());
        classpath.add(new File(agentDictionary, "plugins"));
//        classpath.add(new File(agentDictionary, "activations"));
        // 自定义扩展插件目录配置
        String optionPluginsDir = System.getProperty(Constants.EXTEND_PLUGINS_DIR);
        if (StringUtil.isEmpty(optionPluginsDir)) {
            optionPluginsDir = EXTEND_PLUGIN_DEFAULT_DIR;
        }
        File extendPluginDir = new File(optionPluginsDir);
        if(extendPluginDir.exists()) {
            classpath.add(extendPluginDir);
        }
    }

    /**
     * 加载jar包
     */
    private static void initJar() {
        for (File path : classpath) {
            if (!path.exists() || !path.isDirectory()) {
                continue;
            }
            String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
            if(jarFileNames == null) {
                continue;
            }
            for (String fileName : jarFileNames) {
                try {
                    File file = new File(path, fileName);
                    Jar jar = new Jar(new JarFile(file), file);
                    allJars.add(jar);
                    log.info("{} loaded.", file.toString());
                } catch (IOException e) {
                    log.error(e, "{} jar file can't be resolved", fileName);
                }
            }
        }
    }

    public static AgentClassLoader getDefault() {
        if (DEFAULT_LOADER != null) {
            return DEFAULT_LOADER;
        }
        synchronized (AgentClassLoader.class) {
            if (DEFAULT_LOADER == null) {
                DEFAULT_LOADER = getCacheClassLoader(AgentClassLoader.class.getClassLoader());
            }
        }
        return DEFAULT_LOADER;
    }

    public static AgentClassLoader getCacheClassLoader(ClassLoader parent) {
        return CLASS_LOADERS.computeIfAbsent(parent, AgentClassLoader::new);
    }

    public AgentClassLoader(ClassLoader parent) {
        super(parent);
        log.info("create AgentClassLoader:{}", Util.join(" > ", Util.listClassLoaders(parent)));
//        classLoaders =  new ArrayList<>();
////        classLoaders.add(parent);
//        if(extClassLoader != null && extClassLoader.length > 0) {
//        	classLoaders.addAll(Arrays.asList(extClassLoader));
//        }
//        System.err.println("parent classLoader:" + parent);
    }

//    @Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException {
//    	if(classLoaders.isEmpty()) {
//    		return super.loadClass(name);
//    	}
    	
//    	List<ClassLoader> thisClassLoaders = new ArrayList<>();
//    	thisClassLoaders.add(getParent());
//    	thisClassLoaders.addAll(classLoaders);
//    	
//    	Class<?> cls = null;
//    	ClassNotFoundException exception = null;
//    	for(ClassLoader cl : thisClassLoaders) {
//    		try {
//    			cls = cl.loadClass(name);
//    		} catch (ClassNotFoundException e) {
//    			if(exception == null) {
//    				exception = e;
//    			}
//    		}
//    	}
//    	if(cls == null && exception != null) {
//    		throw exception;
//    	}
//		return cls;
//	}
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry != null) {
                try {
                    URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                    byte[] data;
                    BufferedInputStream is = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        is = new BufferedInputStream(classFileUrl.openStream());
                        baos = new ByteArrayOutputStream();
                        int ch;
                        while ((ch = is.read()) != -1) {
                            baos.write(ch);
                        }
                        data = baos.toByteArray();
                    } finally {
                        if (is != null)
                            try {
                                is.close();
                            } catch (IOException ignored) {
                            }
                        if (baos != null)
                            try {
                                baos.close();
                            } catch (IOException ignored) {
                            }
                    }
                    return defineClass(name, data, 0, data.length);
                } catch (IOException e) {
                	log.error(e, "find class fail.");
                }
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    @Override
    protected URL findResource(String name) {
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry == null) {
                continue;
            }
            try {
                return new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name);
            } catch (MalformedURLException ignored) {}
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<>();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }
        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    private static class Jar {
        private JarFile jarFile;
        private File sourceFile;

        private Jar(JarFile jarFile, File sourceFile) {
            this.jarFile = jarFile;
            this.sourceFile = sourceFile;
        }
    }
}
