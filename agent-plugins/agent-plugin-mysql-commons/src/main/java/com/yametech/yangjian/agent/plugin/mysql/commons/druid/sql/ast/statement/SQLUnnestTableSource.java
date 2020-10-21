package com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast.SQLExpr;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast.SQLName;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.visitor.SQLASTVisitor;

public class SQLUnnestTableSource
        extends SQLTableSourceImpl {
    private final List<SQLExpr> items = new ArrayList<SQLExpr>();
    protected List<SQLName> columns = new ArrayList<SQLName>();
    private boolean ordinality = false;

    public SQLUnnestTableSource() {

    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, items);
            acceptChild(v, columns);
        }
        v.endVisit(this);
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void addColumn(SQLName column) {
        column.setParent(this);
        this.columns.add(column);
    }

    public boolean isOrdinality() {
        return ordinality;
    }

    public void setOrdinality(boolean ordinality) {
        this.ordinality = ordinality;
    }

    public List<SQLExpr> getItems() {
        return items;
    }

    public void addItem(SQLExpr item) {
        item.setParent(this);
        this.items.add(item);
    }

    public SQLUnnestTableSource clone() {
        SQLUnnestTableSource x = new SQLUnnestTableSource();

        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }

        for (SQLName column : columns) {
            SQLName c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }

        x.alias = alias;

        return x;
    }
}
