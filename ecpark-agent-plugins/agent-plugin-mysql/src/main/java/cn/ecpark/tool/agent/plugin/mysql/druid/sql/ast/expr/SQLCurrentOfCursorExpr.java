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

import java.util.Collections;
import java.util.List;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExprImpl;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLName;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLObject;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

public class SQLCurrentOfCursorExpr extends SQLExprImpl {

    private SQLName cursorName;

    public SQLCurrentOfCursorExpr(){

    }

    public SQLCurrentOfCursorExpr(SQLName cursorName){
        this.cursorName = cursorName;
    }

    public SQLCurrentOfCursorExpr clone() {
        SQLCurrentOfCursorExpr x = new SQLCurrentOfCursorExpr();
        if (cursorName != null) {
            x.setCursorName(cursorName.clone());
        }
        return x;
    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName cursorName) {
        if (cursorName != null) {
            cursorName.setParent(this);
        }
        this.cursorName = cursorName;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("CURRENT OF ");
        cursorName.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.cursorName);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        return Collections.singletonList(this.cursorName);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cursorName == null) ? 0 : cursorName.hashCode());
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
        SQLCurrentOfCursorExpr other = (SQLCurrentOfCursorExpr) obj;
        if (cursorName == null) {
            return other.cursorName == null;
        } else return cursorName.equals(other.cursorName);
    }

}
