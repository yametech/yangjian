/**
 * 该包是从开源druid项目中暴力修改抽取出sqlparser的mysql部分代码
 *
 * 专门做sql解析以及表操作统计的
 *
 * 1.去除其他无相关的防注入代码
 * 2.日志依赖改为我们自己自定义的日志逻辑
 *
 * @link https://github.com/alibaba/druid/wiki/SQL-Parser
 */
package com.yametech.yangjian.agent.plugin.mysql.commons.druid;