package com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast;

import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.visitor.SQLASTVisitor;

public class SQLCurrentUserExpr
        extends SQLExprImpl {

    public SQLCurrentUserExpr() {
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        return getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public SQLCurrentUserExpr clone() {
        return new SQLCurrentUserExpr();
    }
}
