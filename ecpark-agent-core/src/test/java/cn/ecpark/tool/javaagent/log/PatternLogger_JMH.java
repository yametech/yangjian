package cn.ecpark.tool.javaagent.log;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.LogLevel;
import cn.ecpark.tool.agent.core.log.LogOutput;
import cn.ecpark.tool.agent.core.log.impl.PatternLogger;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
public class PatternLogger_JMH {

    static {
        Config.setConfig(Config.SERVICE_NAME.getKey(), "PatternLoggerJMH");
        Config.setConfig(Constants.LOG_LEVEL, LogLevel.DEBUG.name());
        Config.setConfig(Constants.LOG_OUTPUT, LogOutput.FILE.name());
        Config.setConfig(Constants.LOG_DIR, "./tmp/logs");
        Config.setConfig(Constants.LOG_MAX_FILE_SIZE, Integer.valueOf(1024 * 1024 * 100).toString());
    }

    private static final PatternLogger logger = new PatternLogger("%timestamp [%level]-[%thread]-[%class.method]: %msg %throwable", PatternLogger_JMH.class);

    @Benchmark
    @BenchmarkMode(Mode.Throughput )
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testDebug(){
        logger.debug("debug");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput )
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testError(){
        logger.error(new NullPointerException(), "ttt");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testArguments(){
        logger.debug("abc {} {}", "def", "ghi");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PatternLogger_JMH.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
