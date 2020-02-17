package cn.ecpark.tool.agent.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import cn.ecpark.tool.agent.core.YMAgent;
import cn.ecpark.tool.agent.core.exception.AgentPathException;

/**
 * @author zcn
 * @date: 2019-10-16
 *
 **/
public class AgentPath {
    private static File file;

    /**
     *   获取agent根目录，子目录为：config、lib、logs、plugins
     */
    public static File getPath(){
        if(file != null) return file;
//        System.err.println(YMAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String classPath = YMAgent.class.getName().replaceAll("\\.", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(classPath);
        String path = resource.toString();

        int insidePathIndex = path.indexOf('!');
        boolean isInJar = insidePathIndex > -1;
        if(isInJar){
            path = path.substring(path.indexOf("file:"), insidePathIndex);
            try {
                return file = new File(new URL(path).toURI()).getParentFile().getParentFile();
            } catch (URISyntaxException | MalformedURLException e) {
                throw new AgentPathException("Fail to parse agent path");
            }
        }else{
            String location = path.substring("file:".length(), path.length() - classPath.length());
            return file = new File(location).getParentFile();
        }
    }

}
