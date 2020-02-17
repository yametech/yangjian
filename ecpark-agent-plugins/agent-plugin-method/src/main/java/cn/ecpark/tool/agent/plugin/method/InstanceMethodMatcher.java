package cn.ecpark.tool.agent.plugin.method;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.ecpark.tool.agent.api.IConfigReader;
import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;

/**
 * 拦截自定义实例方法
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class InstanceMethodMatcher implements IMetricMatcher, IConfigReader {
	private List<IConfigMatch> matches;
	
//	@Override
//    public Set<String> configKey() {
//        return new HashSet<>(Arrays.asList("redis.key.rule", "redis.key.rule\\..*"));
//    }

    /**
     * 覆盖更新
     *
     * @param kv 配置数据
     */
    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        Set<String> configMatchesValues = new HashSet<>();
        kv.entrySet().forEach(entry -> configMatchesValues.addAll(Arrays.asList(entry.getValue().split("\r\n"))));
        matches = configMatchesValues.stream().map(match -> new MethodRegexMatch(match)).collect(Collectors.toList());
    }
	
    @Override
    public IConfigMatch match() {
    	if(matches == null || matches.isEmpty()) {
    		return null;
    	} else {
    		return new CombineOrMatch(matches);
    	}
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.method.InstanceMethodConvert");
    }
}
