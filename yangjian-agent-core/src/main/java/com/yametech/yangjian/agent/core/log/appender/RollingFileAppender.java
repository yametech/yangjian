/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.core.log.appender;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.AppenderFactory;
import com.yametech.yangjian.agent.core.log.IAppender;
import com.yametech.yangjian.agent.core.log.LogEvent;
import com.yametech.yangjian.agent.core.log.LoggerFactory;
import com.yametech.yangjian.agent.core.log.impl.LogMessageHolder;
import com.yametech.yangjian.agent.util.CustomThreadFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zcn
 * @date: 2019-10-14
 * @description: 日志文件实现类
 **/
public class RollingFileAppender implements IAppender<LogEvent>, EventHandler<LogMessageHolder> {

    private static final int RING_BUFFER_SIZE = 512;
    private static final String LOG_FILE_PREFIX = "statistic.";
    private static Pattern LOG_NAME_PATTERN = Pattern.compile("statistic\\.(\\d{8})\\.(\\d+)\\.log");
    private static String DATE_PATTERN = "yyyyMMdd";
    private static final long ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000L;
    private static final String AGENT_LOG_THREAD_PREFIX = "agent-log-writer";
    private RingBuffer<LogMessageHolder> ringBuffer;
    private FileOutputStream fileOutputStream;
    private String dir;
    private String serviceName;
    private long maxFileSize;
    private int maxFileNum;
    private long currentFileSize;
    private int lineNum;

    public RollingFileAppender(String appenderName) {
        this.serviceName = Config.SERVICE_NAME.getValue();
        this.dir = getAppenderDir(appenderName);
        this.maxFileSize = Long.valueOf(Config.getKv(Constants.LOG_MAX_FILE_SIZE, LoggerFactory.DEFAULT_MAX_FILE_SIZE.toString()));
        this.maxFileNum = Integer.valueOf(Config.getKv(Constants.LOG_MAX_FILE_NUM, LoggerFactory.DEFAULT_MAX_FILE_NUM.toString()));

        Disruptor<LogMessageHolder> disruptor = new Disruptor<>(
                () -> new LogMessageHolder(),
                RING_BUFFER_SIZE,
                new CustomThreadFactory(AGENT_LOG_THREAD_PREFIX, true));
        disruptor.handleEventsWith(this::onEvent);
        ringBuffer = disruptor.getRingBuffer();
        lineNum = 0;
        disruptor.start();
    }

    public RollingFileAppender() {
        this(AppenderFactory.ROOT_APPENDER);
    }

    private String getAppenderDir(String appenderName) {
        // 链路日志
        if (AppenderFactory.TRACE_APPENDER.equals(appenderName)) {
            return Config.getKv(Constants.LOG_TRACE_DIR, LoggerFactory.DEFAULT_TRACE_LOG_DIR + serviceName);
        }
        return Config.getKv(Constants.LOG_DIR, LoggerFactory.DEFAULT_DIR) + Constants.PATH_SEPARATOR + serviceName;
    }

    @Override
    public void onEvent(LogMessageHolder msgHolder, long sequence, boolean endOfBatch) {
        if (isStreamEnable()) {
            try {
                write(msgHolder.getMessage(), endOfBatch);
            } catch (IOException e) {
//                e.printStackTrace();
            } finally {
                msgHolder.setMessage(null);
                checkFile();
            }
        }
    }

    @Override
    public void append(LogEvent logEvent) {
        long next = ringBuffer.next();
        try {
            LogMessageHolder messageHolder = ringBuffer.get(next);
            messageHolder.setMessage(logEvent.getMessage());
        } finally {
            ringBuffer.publish(next);
        }
    }

    private void write(String message, boolean endOfBatch) throws IOException {
        message = message + Constants.LINE_SEPARATOR;
        this.fileOutputStream.write(message.getBytes());

        lineNum++;
        currentFileSize += message.length();

        if (lineNum % 100 == 0 || endOfBatch) {
            this.fileOutputStream.flush();
        }
        lineNum = (lineNum == Integer.MAX_VALUE) ? 0 : lineNum;
    }

    private void checkFile() {
        if (currentFileSize > maxFileSize) {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
//                e.printStackTrace();
            } finally {
                fileOutputStream = null;
            }
        }
    }

    private String getFileNamePrefix() {
        return LOG_FILE_PREFIX + new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    private File createFile(int fileIndex) {
        return new File(this.dir, getFileNamePrefix() + "." + fileIndex + ".log");
    }

    private boolean isStreamEnable() {
        if (fileOutputStream != null) {
            return true;
        }

        File dir = new File(this.dir);
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            System.err.println("Log dir " + dir.getAbsoluteFile() + " is not a directory");
            return false;
        }

        try {
            File file = switchFile(dir);
            this.fileOutputStream = new FileOutputStream(file, true);
            this.currentFileSize = file.length();
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    private File switchFile(File dir) throws ParseException {
        File[] children = dir.listFiles();
        if (children.length == 0) {
            return createFile(1);
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        for (int i = 0; i < children.length; i++) {
            File child = children[i];
            String name = child.getName();
            Matcher matcher = LOG_NAME_PATTERN.matcher(name);
            if (matcher.find()) {
                String date = matcher.group(1);
                int num = Integer.valueOf(matcher.group(2));
                fileInfos.add(new FileInfo(child, new SimpleDateFormat(DATE_PATTERN).parse(date), num));
            }
        }

        Collections.sort(fileInfos, (f1, f2) -> f1.date.equals(f2.date) ? f1.num - f2.num : f1.date.compareTo(f2.date));

        int outOf = children.length - maxFileNum;
        if (outOf > 0) {
            for (int i = 0; outOf > 0; outOf--, i++) {
                fileInfos.get(i).file.delete();
            }
        }

        FileInfo latest = fileInfos.get(children.length - 1);
        if (System.currentTimeMillis() - latest.date.getTime() > ONE_DAY_MILLISECONDS) {
            return createFile(1);
        }

        if (latest.file.length() >= maxFileSize) {
            return createFile(latest.num + 1);
        }

        return latest.file;
    }

    private class FileInfo {
        private File file;
        private Date date;
        private Integer num;

        private FileInfo(File file, Date date, Integer num) {
            this.file = file;
            this.date = date;
            this.num = num;
        }
    }
}
