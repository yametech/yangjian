/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.plugin.mysql.common;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.plugin.mysql.bean.SqlBean;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.SQLUtils;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.visitor.SchemaStatVisitor;
import com.yametech.yangjian.agent.plugin.mysql.druid.stat.TableStat;
import com.yametech.yangjian.agent.plugin.mysql.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2019/11/28
 */
public class MysqlUtil {
    private static final String SQL_EXCLUDE_COLUMUN_PREFIX = "@@";
    private static final String SQL_EXCLUDE_WARNING_PREFIX = "WARNINGS";
    private static final String SQL_EXCLUDE_SUFFIX = "where 1 = 2";

    /**
     * 判断sql是否排除
     *
     * 1.过滤查询以@@开头列名的表语句 如：SELECT @@session.transaction_isolation
     * 2.过滤类似SHOW WARNINGS语句
     * 3.过滤包含where 1 = 2的sql 如：select * from xxx where 1 = 2（目前只有jfinal插件才会发出这种sql）
     *
     * @param sql
     * @return
     */
    public static boolean isInExcludeList(String sql) {
        return sql.indexOf(SQL_EXCLUDE_COLUMUN_PREFIX) >= 0
                || sql.indexOf(SQL_EXCLUDE_WARNING_PREFIX) >= 0
                || sql.endsWith(SQL_EXCLUDE_SUFFIX);
    }

    /**
     * 构建sql执行统计事件
     *
     * @param sqlBean
     * @return
     */
    public static List<TimeEvent> buildSqlTimeEvent(SqlBean sqlBean) {
        List<TimeEvent> timeEvents = new ArrayList<>();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sqlBean.getSql(), JdbcConstants.MYSQL);
        if (stmtList != null && stmtList.size() > 0) {
            // 只处理第一条
            SQLStatement stmt = stmtList.get(0);
            // 统计SQL中使用的表、操作
            SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
            stmt.accept(statVisitor);

            // 遍历获取每张表的delete、update、insert、select次数
            for (Map.Entry<TableStat.Name, TableStat> entry : statVisitor.getTables().entrySet()) {
                TableStat tableStat = entry.getValue();
                if (tableStat.getInsertCount() > 0) {
                    timeEvents.add(initMysqlTimeEvent(sqlBean, Constants.EventType.MYSQL_TABLE, entry.getKey() +
                            Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.INSERT, tableStat.getInsertCount()));
                }
                if (tableStat.getUpdateCount() > 0) {
                    timeEvents.add(initMysqlTimeEvent(sqlBean, Constants.EventType.MYSQL_TABLE, entry.getKey() +
                            Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.UPDATE, tableStat.getUpdateCount()));
                }
                if (tableStat.getSelectCount() > 0) {
                    timeEvents.add(initMysqlTimeEvent(sqlBean, Constants.EventType.MYSQL_TABLE, entry.getKey() +
                            Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.SELECT, tableStat.getSelectCount()));
                }
                if (tableStat.getDeleteCount() > 0) {
                    timeEvents.add(initMysqlTimeEvent(sqlBean, Constants.EventType.MYSQL_TABLE, entry.getKey() +
                            Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.DELETE, tableStat.getDeleteCount()));
                }
            }

            // 参数化sql 如：select * from t where id = 1会变成select * from t where id = ?, 方便sql统计
            String parameterizedSql = ParameterizedOutputVisitorUtils.parameterize(sqlBean.getSql(), JdbcConstants.MYSQL);
            timeEvents.add(initMysqlTimeEvent(sqlBean, Constants.EventType.MYSQL_SQL, parameterizedSql, 1));
        }
        return timeEvents;
    }

    private static TimeEvent initMysqlTimeEvent(SqlBean sqlBean, String type, String identify, long num) {
        TimeEvent timeEvent = new TimeEvent();
        timeEvent.setEventTime(sqlBean.getEventTime());
        timeEvent.setUseTime(sqlBean.getUseTime());
        timeEvent.setIdentify(identify);
        timeEvent.setNumber(num);
        // 区分sql统计跟表操作统计
        timeEvent.setType(type);
        return timeEvent;
    }
}
