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
package com.yametech.yangjian.agent.server.storage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author dengliming
 * @date 2020/3/9
 */
@Component
public class DiskMetricStore {

    private static final Logger METRICS = LoggerFactory.getLogger("Metrics");
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskMetricStore.class);
    private static Pattern LOG_NAME_PATTERN = Pattern.compile("metrics\\.(\\d{8})\\.(\\d+)\\.log");
    @Value("${metric.path:/data/logs/metrics/}")
    private String metricPath;
    @Value("${metric.log.name:metrics.log}")
    private String firstLogName;
    @Value("${metric.fetch.limit:50}")
    private int limit;
    @Value("${metric.position.path:/data/config/position.log}")
    private String positionPath;
    private String lastReadFile = "";
    private int lastReadLine = 0;
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        // 初始化
        initPosition();
        scheduler = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                .namingPattern("MetricStore-%d").daemon(true).build());
        scheduler.scheduleAtFixedRate(() -> flushPosition(), 5, 5, TimeUnit.SECONDS);
    }

    private void initPosition() {
        try {
            Path path = Paths.get(positionPath);
            if (!Files.exists(path)) {
                return;
            }

            String lastPosition = null;
            try (Stream<String> allLines = Files.lines(path)) {
                lastPosition = allLines.findFirst().get();
            }
            if (StringUtils.isNotBlank(lastPosition)) {
                String[] positions = lastPosition.split("\\|");
                if (positions.length == 2) {
                    lastReadFile = positions[0];
                    lastReadLine = NumberUtils.toInt(positions[1]);
                }
            }
        } catch (IOException e) {
            LOGGER.error("initPosition error.", e);
        }
    }

    /**
     * 保存读取位置到磁盘
     */
    private void flushPosition() {
        if (StringUtils.isBlank(lastReadFile)) {
            return;
        }
        try {
            Files.write(Paths.get(positionPath), (lastReadFile + "|" + lastReadLine).getBytes());
        } catch (IOException e) {
            LOGGER.error("flushPosition error.", e);
        }
    }

    /**
     * 防止并发获取（通常不会发生，因为只提供prometheus定时采集调用）
     *
     * @return
     * @throws IOException
     */
    public synchronized List<String> getMetrics() throws IOException {
        if (StringUtils.isBlank(lastReadFile)) {
            // 首次读取
            MetricFile metricFile = getNextOneFile();
            if (metricFile == null) {
                return null;
            }
            lastReadFile = metricFile.getFileName();
            lastReadLine = limit;
            return read(metricPath + lastReadFile, 0, limit);
        }

        List<String> metrics = read(metricPath + lastReadFile, lastReadLine, limit);
        // 如果为空判断是否产生了新文件
        if (CollectionUtils.isEmpty(metrics)) {
            MetricFile metricFile = getNextOneFile();
            // 没有产生新文件则是没有数据直接返回
            if (metricFile == null) {
                return null;
            }

            // 新文件则重新初始化读取位置
            lastReadFile = metricFile.getFileName();
            lastReadLine = limit;
            return read(metricPath + lastReadFile, 0, limit);
        }
        lastReadLine += metrics.size();
        return metrics;
    }

    private List<String> read(String path, int startLine, int limit) {
        long s = System.currentTimeMillis();
        List<String> lines = null;
        try (Stream<String> allLines = Files.lines(Paths.get(path))) {
            lines = allLines.skip(startLine).limit(limit).collect(toList());
        } catch (IOException e) {
            LOGGER.error("read(path:{},startLine:{},limit:{}) error", path, startLine, limit, e);
        }
        LOGGER.info("read({}|{}|{}) done in {}ms", path, startLine, limit, System.currentTimeMillis() - s);
        return lines;
    }

    public void write(String metric) {
        METRICS.info(metric);
    }

    public MetricFile getNextOneFile() throws IOException {
        try (Stream<Path> allPaths = Files.list(Paths.get(metricPath))) {
            MetricFile currentMetricFile = extractMetricFile(lastReadFile);
            List<MetricFile> metricFiles = new ArrayList<>();
            metricFiles.addAll(allPaths.map(it -> {
                String fileName = it.getFileName().toString();
                return extractMetricFile(fileName);
            }).filter(it -> {
                if (it == null) {
                    return false;
                }
                if (currentMetricFile == null) {
                    return true;
                }
                // 如果已有读取文件记录，则只返回晚于该文件的
                return (currentMetricFile.getDay() == it.getDay() && currentMetricFile.getNum() < it.getNum())
                        || (currentMetricFile.getDay() < it.getDay());
            }).collect(toList()));
            Collections.sort(metricFiles, (o1, o2) -> {
                if (firstLogName.equals(o1.getFileName())) {
                    return -1;
                }
                if (firstLogName.equals(o2.getFileName())) {
                    return 1;
                }

                return o1.getDay() == o1.getDay() ? o1.getNum() - o2.getNum() : o1.getDay() - o2.getDay();
            });
            return metricFiles.isEmpty() ? null : metricFiles.get(0);
        }
    }

    private MetricFile extractMetricFile(String fileName) {
        if (firstLogName.equals(fileName)) {
            return new MetricFile(fileName, 0, 0);
        }
        Matcher matcher = LOG_NAME_PATTERN.matcher(fileName);
        if (matcher.find()) {
            String date = matcher.group(1);
            int num = Integer.valueOf(matcher.group(2));
            return new MetricFile(fileName, Integer.parseInt(date), num);
        }
        return null;
    }

    @PreDestroy
    public void destory() {
        flushPosition();
        try {
            scheduler.shutdown();
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("destory error.", e);
        }
    }
}
