package cn.ecpark.tool.agent.plugin.redisson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.redisson.client.protocol.CommandData;
import org.redisson.client.protocol.CommandsData;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodAsyncConvert;
import cn.ecpark.tool.agent.plugin.redisson.bean.RedisKeyBean;
import io.netty.buffer.ByteBuf;

/**
 * 转换redission事件
 *
 * @author dengliming
 * @date 2019/12/5
 */
public class RedissionConvert implements IMethodAsyncConvert {
    private RedissionMatcher redissionMatcher;
    
    @Override
    public void setMetricMatcher(IMetricMatcher metricMatcher) {
    	redissionMatcher = (RedissionMatcher) metricMatcher;
    }
    
    @Override
    public List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
    		Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (allArguments == null || allArguments[0] == null) {
            return null;
        }
        List<CommandData> commandDatas = new ArrayList<>();
        if (allArguments[0] instanceof CommandsData) {
            CommandsData commands = (CommandsData) allArguments[0];
            if (commands.getCommands() != null) {
                commandDatas.addAll(commands.getCommands());
            }
        } else if (allArguments[0] instanceof CommandData) {
            commandDatas.add((CommandData) allArguments[0]);
        }

        List<String> keys = new ArrayList<>();
        for (CommandData commandData : commandDatas) {
            String command = commandData.getCommand().getName();
            // 过滤执行lua脚本命令
            if (isEvalScript(command)) {
                continue;
            }
            String key = getCommandKey(commandData);
            if (StringUtil.notEmpty(key)) {
                keys.add(key);
            }
        }
        long now = System.currentTimeMillis();
        return Arrays.asList(new RedisKeyBean(keys, now, now - startTime));
    }
    
    @Override
    public List<TimeEvent> convert(Object eventBean) {
        RedisKeyBean redisKeyBean = (RedisKeyBean) eventBean;
        return redissionMatcher.getMatchKeyTimeEvents(redisKeyBean);
    }
    
    /**
     * 获取redis执行命令的key
     *
     * @param commandData
     * @return
     */
    private String getCommandKey(CommandData commandData) {
        Object[] params = commandData.getParams();
        if (params != null && params.length > 0) {
            return params[0] instanceof ByteBuf ? "?" : String.valueOf(params[0].toString());
        }
        return null;
    }

    /**
     * 判断是否是执行lua脚本的命令
     *
     * @param command
     * @return
     */
    public boolean isEvalScript(String command) {
        return command.equalsIgnoreCase("EVAL");
    }
}
