package cn.ecpark.tool.agent.api.base;

/**
 * 仅做spi加载使用，通过无参构造方法实例化，所以实现类必须包含无参构造方法，只要支持实现了该接口，并且配置到services，即可被加载到，目前包含的功能型接口有
 * 		IEventConvert
 * 		IMethodAOP
 * 		IMethodEventListener
 * 		ISchedule
 * 		IAppStatusListener
 * 		IConfigReader
 * 接口可实现的功能参考接口注释，实现类可实现上述的多个接口同时拥有多个接口的功能，必须通过SPI的方式实例化，否则不会具备对应的功能
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月10日 下午10:38:02
 */
public interface SPI {

}
