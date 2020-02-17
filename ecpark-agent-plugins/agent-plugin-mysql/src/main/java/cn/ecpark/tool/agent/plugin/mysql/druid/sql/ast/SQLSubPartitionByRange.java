/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.expr.SQLIdentifierExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

public class SQLSubPartitionByRange extends SQLSubPartitionBy {
    private List<SQLName> columns = new ArrayList<SQLName>();

    public List<SQLName> getColumns() {
        return columns;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        
    }

    public SQLSubPartitionByRange clone() {
        SQLSubPartitionByRange x = new SQLSubPartitionByRange();

        for (SQLName column : columns) {
            SQLName c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        return x;
    }

    public boolean isPartitionByColumn(long columnNameHashCode64) {
        for (SQLExpr column : columns) {
            if (column instanceof SQLIdentifierExpr
                    && ((SQLIdentifierExpr) column).nameHashCode64() == columnNameHashCode64) {
                return true;
            }
        }
        return false;
    }
}
