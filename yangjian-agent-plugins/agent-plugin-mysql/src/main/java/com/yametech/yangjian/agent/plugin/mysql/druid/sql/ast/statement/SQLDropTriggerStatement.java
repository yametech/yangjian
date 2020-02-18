package com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLName;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLObject;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLStatementImpl;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.visitor.SQLASTVisitor;

public class SQLDropTriggerStatement extends SQLStatementImpl implements SQLDropStatement {

    private SQLName name;
    private boolean ifExists;

    public SQLDropTriggerStatement() {
        
    }
    
    public SQLDropTriggerStatement(String dbType) {
        super (dbType);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        return children;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }
}
