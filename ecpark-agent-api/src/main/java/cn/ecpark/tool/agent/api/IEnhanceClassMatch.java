package cn.ecpark.tool.agent.api;

import cn.ecpark.tool.agent.api.base.IConfigMatch;

/**
 * 继承该接口后会自动增强classMatch匹配的类
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月20日 下午6:18:20
 */
public interface IEnhanceClassMatch {
	
	/**
	 * 返回类匹配配置
	 * @return
	 */
	IConfigMatch classMatch();
}
