package cn.ecpark.tool.agent.core.aop.consume;

import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cn.ecpark.tool.agent.api.IAppStatusListener;
import cn.ecpark.tool.agent.api.ISchedule;
import cn.ecpark.tool.agent.api.convert.statistic.impl.BaseStatistic;
import cn.ecpark.tool.agent.core.aop.base.ConvertTimeEvent;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.LogUtil;
import cn.ecpark.tool.agent.util.eventbus.consume.BaseConsume;
import cn.ecpark.tool.agent.util.eventbus.consume.ConsumeFactory;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class RTEventListener implements IAppStatusListener, ConsumeFactory<ConvertTimeEvent>, ISchedule {
    private static final ILogger log = LoggerFactory.getLogger(RTEventListener.class);
    private List<RTEventConsume> consumes = new ArrayList<>();
//    private Map<String, String> configMatches = new HashMap<>();
//    private List<IConfigMatch> matches = new ArrayList<>();

//    @Override
//    public Set<String> configKey() {
//        return new HashSet<>(Arrays.asList("RTEventListener", "RTEventListener\\..*"));
//    }
//
//    @Override
//    public void configKeyValue(Map<String, String> kv) {
//        if (kv == null) {
//            return;
//        }
//        this.configMatches = kv;
////		kv.entrySet().stream()
//////			.filter(value -> value != null && !value.trim().isEmpty())
////			.forEach(value -> configMatches.put(value.getKey(), new Set)(Arrays.asList(value.split("\r\n"))));
//    }

    @Override
    public void beforeRun() {
//        Set<String> configMatchesValues = new HashSet<>();
//        configMatches.entrySet().forEach(entry -> configMatchesValues.addAll(Arrays.asList(entry.getValue().split("\r\n"))));
//        configMatchesValues.forEach(match -> matches.add(new MethodRegexMatch(match)));
    }

//    @Override
//    public IConfigMatch match() {
//        return new CombineOrMatch(matches);
//    }

    @Override
    public BaseConsume<ConvertTimeEvent> getConsume() {
        // 一个实例为单线程消费，如果此处返回同一个实例且配置了多线程消费，则实例为多线程消费
        RTEventConsume consume = new RTEventConsume();
        consumes.add(consume);
        return consume;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public void execute() {
        LogUtil.println("method-event/consume", false,
                new SimpleEntry<String, Object>("total_num", getTotalNum()),
                new SimpleEntry<String, Object>("period_seconds", interval()),
                new SimpleEntry<String, Object>("period_num", getPeriodNum()));
        // 循环consumes输出累加统计值，或者直接输出每个的统计值，ecpark-monitor做聚合
        for (RTEventConsume consume : consumes) {
            for (BaseStatistic statistic : consume.getReportStatistics()) {
                Entry<String, Object>[] kvs = statistic.kv();
                if (kvs != null) {
                    LogUtil.println(statistic.getSecond(),
                            "statistic/" + statistic.getType() + "/" + statistic.statisticType(), true, statistic.kv());
                }
            }
        }
    }

    private int getTotalNum() {
        int totalNum = 0;
        for (RTEventConsume consume : consumes) {
            totalNum += consume.getTotalNum();
        }
        return totalNum;
    }

    private int getPeriodNum() {
        int periodNum = 0;
        for (RTEventConsume consume : consumes) {
            periodNum += consume.getPeriodTotalNum();
        }
        return periodNum;
    }

    @Override
    public boolean shutdown(Duration duration) {
        long previousNum = 0;
        while (previousNum < getTotalNum()) {// N毫秒内无调用事件则关闭，避免因关闭服务导致事件丢失
            try {
                previousNum = getTotalNum();
                Thread.sleep(interval() * 1002L);
            } catch (InterruptedException e) {
                log.warn(e, "shutdown interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
        return true;
    }

//	@Override
//	public int parallelism() {
//		return 3;
//	}
}
