package cn.ecpark.tool.agent.core.core.agent;

import java.util.Map;

public interface IContextField {
	Map<String, Object> __getAgentContext();
	
	void __setAgentContext(Map<String, Object> context);
}
