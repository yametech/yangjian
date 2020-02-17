package cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

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
