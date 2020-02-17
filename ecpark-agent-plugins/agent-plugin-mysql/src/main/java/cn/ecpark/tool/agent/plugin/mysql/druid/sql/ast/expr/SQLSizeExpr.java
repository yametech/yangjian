/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExprImpl;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLObject;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

public class SQLSizeExpr
        extends SQLExprImpl {

    private SQLExpr value;
    private Unit    unit;

    public SQLSizeExpr(){

    }

    public SQLSizeExpr(String value, char unit){
        this.unit = Unit.valueOf(Character.toString(unit).toUpperCase());
        if (value.indexOf('.') == -1) {
            this.value = new SQLIntegerExpr(Integer.parseInt(value));
        } else {
            this.value = new SQLNumberExpr(new BigDecimal(value));
        }
    }

    public SQLSizeExpr(SQLExpr value, Unit unit){
        super();
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.value != null) {
                this.value.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        return Collections.singletonList(value);
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public enum Unit {
        B, K, M, G, T, P, E
    }

    public SQLSizeExpr clone() {
        SQLSizeExpr x = new SQLSizeExpr();

        if (value != null) {
            x.setValue(value.clone());
        }
        x.unit = unit;

        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLSizeExpr that = (SQLSizeExpr) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return unit == that.unit;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
