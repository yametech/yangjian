# readme放在此处方便打包时带上，提供给研发看

## 介绍
该包用于应用定制监控及链路，主要包含如下功能：
### 服务状态检测
* 接口：com.yametech.yangjian.agent.client.IStatusCollect
* 说明：通过实现接口并接入探针，会自动定时调用collect方法收集服务自定义监控数据，如果返回状态为ERROR则触发告警，并显示对应的reason
### 链路定制
* 类方法：com.yametech.yangjian.agent.client.TraceUtil.mark
* 说明：使用该方法执行业务逻辑，会自动生成对应的链路信息，相对于使用插件的方式定制链路门槛更低，但是并不能替代插件的方式，这种方式仅适用于研发开发的代码，无法对jar包中的代码做更改