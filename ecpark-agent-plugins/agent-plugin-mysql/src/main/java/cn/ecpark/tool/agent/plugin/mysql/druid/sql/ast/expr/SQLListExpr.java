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
package cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.expr;

import java.util.ArrayList;
import java.util.List;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExprImpl;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

public class SQLListExpr extends SQLExprImpl {

    private final List<SQLExpr> items = new ArrayList<SQLExpr>();

    public List<SQLExpr> getItems() {
        return items;
    }
    
    public void addItem(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLListExpr other = (SQLListExpr) obj;
        if (items == null) {
            return other.items == null;
        } else return items.equals(other.items);
    }

    public SQLListExpr clone() {
        SQLListExpr x = new SQLListExpr();
        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        return x;
    }

    public List getChildren() {
        return this.items;
    }
}
