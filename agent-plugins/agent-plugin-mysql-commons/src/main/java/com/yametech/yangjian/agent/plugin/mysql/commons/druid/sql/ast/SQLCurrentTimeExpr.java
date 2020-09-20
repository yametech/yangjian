package com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast;

import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.visitor.SQLASTVisitor;

public class SQLCurrentTimeExpr
        extends SQLExprImpl {
    private final Type type;

    public SQLCurrentTimeExpr(Type type) {
        if (type == null) {
            throw new NullPointerException();
        }

        this.type = type;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        SQLCurrentTimeExpr that = (SQLCurrentTimeExpr) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public SQLCurrentTimeExpr clone() {
        return new SQLCurrentTimeExpr(type);
    }

    public enum Type {
        CURRENT_TIME("CURRENT_TIME"),
        CURRENT_DATE("CURRENT_DATE"),
        CURDATE("CURDATE"),
        CURTIME("CURTIME"),
        CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
        LOCALTIME("LOCALTIME"),
        LOCALTIMESTAMP("LOCALTIMESTAMP"),
        SYSDATE("SYSDATE"),
        ;

        public final String name;
        public final String name_lower;

        Type(String name)
        {
            this.name = name;
            this.name_lower = name.toLowerCase();
        }
    }
}
