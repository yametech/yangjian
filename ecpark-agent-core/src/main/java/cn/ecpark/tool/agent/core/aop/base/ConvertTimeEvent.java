package cn.ecpark.tool.agent.core.aop.base;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.IAsyncConvert;

public class ConvertTimeEvent extends TimeEvent {
	private IAsyncConvert convert;
	private Object data;
	
	public IAsyncConvert getConvert() {
		return convert;
	}
	
	public void setConvert(IAsyncConvert convert) {
		this.convert = convert;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
}
