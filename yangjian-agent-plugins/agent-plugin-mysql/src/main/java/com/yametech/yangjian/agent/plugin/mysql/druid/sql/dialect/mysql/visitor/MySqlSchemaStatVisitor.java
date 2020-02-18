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
package com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.visitor;

import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLName;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.SQLObject;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLAlterCharacter;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLAlterTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLCreateTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLExprTableSource;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLStartTransactionStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLTableSource;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.ast.statement.SQLUpdateStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlKey;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterEventStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterLogFileGroupStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterServerStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTablespaceStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlChecksumTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateAddLogFileGroupStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateEventStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateServerStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableSpaceStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlEventSchedule;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasePartitionStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.yametech.yangjian.agent.plugin.mysql.druid.sql.visitor.SchemaStatVisitor;
import com.yametech.yangjian.agent.plugin.mysql.druid.stat.TableStat;
import com.yametech.yangjian.agent.plugin.mysql.druid.stat.TableStat.Mode;
import com.yametech.yangjian.agent.plugin.mysql.druid.util.JdbcConstants;
import com.yametech.yangjian.agent.plugin.mysql.druid.util.JdbcUtils;

public class MySqlSchemaStatVisitor extends SchemaStatVisitor implements MySqlASTVisitor {

    public MySqlSchemaStatVisitor() {
        super (JdbcConstants.MYSQL);
    }

    public boolean visit(SQLSelectStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        return true;
    }

    @Override
    public String getDbType() {
        return JdbcUtils.MYSQL;
    }

    // DUAL
    public boolean visit(MySqlDeleteStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            from.accept(this);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            using.accept(this);
        }

        SQLTableSource tableSource = x.getTableSource();
        tableSource.accept(this);

        if (tableSource instanceof SQLExprTableSource) {
            TableStat stat = this.getTableStat((SQLExprTableSource) tableSource);
            stat.incrementDeleteCount();
        }

        accept(x.getWhere());

        accept(x.getOrderBy());
        accept(x.getLimit());

        return false;
    }

    public void endVisit(MySqlDeleteStatement x) {
    }

    @Override
    public void endVisit(MySqlInsertStatement x) {
        setModeOrigin(x);
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        setMode(x, Mode.Insert);

        TableStat stat = getTableStat(x.getTableSource());

        if (stat != null) {
            stat.incrementInsertCount();
        }

        accept(x.getColumns());
        accept(x.getValuesList());
        accept(x.getQuery());
        accept(x.getDuplicateKeyUpdate());

        return false;
    }

    @Override
    public boolean visit(MySqlTableIndex x) {

        return false;
    }

    @Override
    public void endVisit(MySqlTableIndex x) {

    }

    @Override
    public boolean visit(MySqlKey x) {
        for (SQLObject item : x.getColumns()) {
            item.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlKey x) {

    }

    @Override
    public boolean visit(MySqlPrimaryKey x) {
        for (SQLSelectOrderByItem item : x.getColumns()) {
            SQLExpr expr = item.getExpr();
            expr.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlPrimaryKey x) {

    }

    @Override
    public void endVisit(MySqlExtractExpr x) {

    }

    @Override
    public boolean visit(MySqlExtractExpr x) {

        return true;
    }

    @Override
    public void endVisit(MySqlMatchAgainstExpr x) {

    }

    @Override
    public boolean visit(MySqlMatchAgainstExpr x) {

        return true;
    }

    @Override
    public void endVisit(MySqlPrepareStatement x) {

    }

    @Override
    public boolean visit(MySqlPrepareStatement x) {

        return true;
    }

    @Override
    public void endVisit(MySqlExecuteStatement x) {

    }

    @Override
    public boolean visit(MySqlExecuteStatement x) {

        return true;
    }
    
    @Override
    public void endVisit(MysqlDeallocatePrepareStatement x) {
    	
    }
    
    @Override
    public boolean visit(MysqlDeallocatePrepareStatement x) {
    	return true;
    }

    @Override
    public void endVisit(MySqlLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadDataInFileStatement x) {

        return true;
    }

    @Override
    public void endVisit(MySqlLoadXmlStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadXmlStatement x) {

        return true;
    }

    @Override
    public void endVisit(SQLStartTransactionStatement x) {

    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {

        return true;
    }

    @Override
    public void endVisit(MySqlShowColumnsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowColumnsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowDatabasesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDatabasesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowWarningsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowWarningsStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlShowStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        return true;
    }

    @Override
    public void endVisit(MySqlKillStatement x) {

    }

    @Override
    public boolean visit(MySqlKillStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlBinlogStatement x) {

    }

    @Override
    public boolean visit(MySqlBinlogStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlResetStatement x) {

    }

    @Override
    public boolean visit(MySqlResetStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlCreateUserStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateUserStatement x) {
        return false;
    }

    @Override
    public void endVisit(UserSpecification x) {

    }

    @Override
    public boolean visit(UserSpecification x) {
        return true;
    }

    @Override
    public void endVisit(MySqlPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlPartitionByKey x) {
        accept(x.getColumns());
        return false;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return this.visit((SQLSelectQueryBlock) x);
    }

    @Override
    public void endVisit(MySqlSelectQueryBlock x) {
        super.endVisit(x);
    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        return false;
    }

    @Override
    public void endVisit(MySqlOutFileExpr x) {

    }

    @Override
    public boolean visit(MySqlExplainStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        SQLName tableName = x.getTableName();
        if (tableName != null) {
            String table = tableName.toString();
            getTableStat(tableName);

            SQLName columnName = x.getColumnName();
            if (columnName != null) {
                addColumn(table, columnName.getSimpleName());
            }
        }

        if (x.getStatement() != null) {
            accept(x.getStatement());
        }

        return false;
    }

    @Override
    public void endVisit(MySqlExplainStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        visit((SQLUpdateStatement) x);
        for (SQLExpr item : x.getReturning()) {
            item.accept(this);
        }
        
        return false;
    }

    @Override
    public void endVisit(MySqlUpdateStatement x) {

    }

    @Override
    public boolean visit(MySqlSetTransactionStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlSetTransactionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowAuthorsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinaryLogsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowMasterLogsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCollationStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCollationStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinLogEventsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCharacterSetStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(MySqlShowContributorsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowContributorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateViewStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEngineStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowEngineStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEnginesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowEnginesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowErrorsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowErrorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEventsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowFunctionStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowGrantsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowGrantsStatement x) {

    }

    @Override
    public boolean visit(MySqlUserName x) {
        return false;
    }

    @Override
    public void endVisit(MySqlUserName x) {

    }

    @Override
    public boolean visit(MySqlShowIndexesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowIndexesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowKeysStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowKeysStatement x) {

    }

    @Override
    public boolean visit(MySqlShowMasterStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowOpenTablesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPluginsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowPluginsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcedureStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcessListStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcessListStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfileStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowProfileStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfilesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowProfilesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRelayLogEventsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowRelayLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveHostsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTriggersStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowTriggersStatement x) {

    }

    @Override
    public boolean visit(MySqlShowVariantsStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowVariantsStatement x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement.Item x) {
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement x) {

    }

    @Override
    public boolean visit(MySqlUseIndexHint x) {
        return false;
    }

    @Override
    public void endVisit(MySqlUseIndexHint x) {

    }

    @Override
    public boolean visit(MySqlIgnoreIndexHint x) {
        return false;
    }

    @Override
    public void endVisit(MySqlIgnoreIndexHint x) {

    }

    @Override
    public boolean visit(MySqlLockTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlLockTableStatement x) {

    }

    @Override
    public boolean visit(MySqlLockTableStatement.Item x) {
        return false;
    }

    @Override
    public void endVisit(MySqlLockTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlUnlockTablesStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlForceIndexHint x) {
        return false;
    }

    @Override
    public void endVisit(MySqlForceIndexHint x) {

    }

    @Override
    public boolean visit(MySqlAlterTableChangeColumn x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();

        SQLName table = stmt.getName();
        String tableName = table.toString();

        SQLName column = x.getColumnName();
        String columnName = column.toString();
        addColumn(tableName, columnName);
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableModifyColumn x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();

        SQLName table = stmt.getName();
        String tableName = table.toString();

        SQLName column = x.getNewColumnDefinition().getName();
        String columnName = column.toString();
        addColumn(tableName, columnName);

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(SQLAlterCharacter x) {
        return false;
    }

    @Override
    public void endVisit(SQLAlterCharacter x) {

    }

    @Override
    public boolean visit(MySqlAlterTableOption x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableOption x) {

    }

    @Override
    public boolean visit(MySqlCreateTableStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        boolean val = super.visit(x);

        for (SQLObject option : x.getTableOptions().values()) {
            if (option instanceof SQLTableSource) {
                option.accept(this);
            }
        }

        return val;
    }

    @Override
    public void endVisit(MySqlCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlHelpStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlHelpStatement x) {

    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        return false;
    }

    @Override
    public void endVisit(MySqlCharExpr x) {

    }

    @Override
    public boolean visit(MySqlUnique x) {
        return false;
    }

    @Override
    public void endVisit(MySqlUnique x) {

    }

    @Override
    public boolean visit(MysqlForeignKey x) {
        return super.visit(x);
    }

    @Override
    public void endVisit(MysqlForeignKey x) {

    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(TableSpaceOption x) {
        return false;
    }

    @Override
    public void endVisit(TableSpaceOption x) {
    }

    @Override
    public boolean visit(MySqlAnalyzeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAnalyzeStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlAlterUserStatement x) {

    }

    @Override
    public boolean visit(MySqlOptimizeStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlOptimizeStatement x) {

    }

    @Override
    public boolean visit(MySqlHintStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlHintStatement x) {

    }

    @Override
    public boolean visit(MySqlOrderingExpr x) {
        return true;
    }

    @Override
    public void endVisit(MySqlOrderingExpr x) {

    }

    @Override
    public boolean visit(MySqlAlterTableAlterColumn x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(MySqlCaseStatement x) {
        accept(x.getWhenList());
        return false;
    }

    @Override
    public void endVisit(MySqlCaseStatement x) {

    }

    @Override
    public boolean visit(MySqlDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlDeclareStatement x) {

    }

    @Override
    public boolean visit(MySqlSelectIntoStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlSelectIntoStatement x) {

    }

    @Override
    public boolean visit(MySqlWhenStatement x) {
        accept(x.getStatements());
        return false;
    }

    @Override
    public void endVisit(MySqlWhenStatement x) {

    }

    @Override
    public boolean visit(MySqlLeaveStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlLeaveStatement x) {

    }

    @Override
    public boolean visit(MySqlIterateStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlIterateStatement x) {

    }

    @Override
    public boolean visit(MySqlRepeatStatement x) {
        accept(x.getStatements());
        return false;
    }

    @Override
    public void endVisit(MySqlRepeatStatement x) {

    }

    @Override
    public boolean visit(MySqlCursorDeclareStatement x) {
        accept(x.getSelect());
        return false;
    }

    @Override
    public void endVisit(MySqlCursorDeclareStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateTableSource x) {
        if (x.getUpdate() != null) {
            return this.visit(x.getUpdate());
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUpdateTableSource x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByKey x) {
        return false;
    }

    @Override
    public void endVisit(MySqlSubPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByList x) {
        return false;
    }

    @Override
    public void endVisit(MySqlSubPartitionByList x) {

    }

	@Override
	public boolean visit(MySqlDeclareHandlerStatement x) {
		return false;
	}

	@Override
	public void endVisit(MySqlDeclareHandlerStatement x) {

	}

	@Override
	public boolean visit(MySqlDeclareConditionStatement x) {
		return false;
	}

	@Override
	public void endVisit(MySqlDeclareConditionStatement x) {

	}

    @Override
    public boolean visit(MySqlFlushStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlFlushStatement x) {

    }

    @Override
    public boolean visit(MySqlEventSchedule x) {
        return false;
    }

    @Override
    public void endVisit(MySqlEventSchedule x) {

    }

    @Override
    public boolean visit(MySqlCreateEventStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlCreateAddLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateServerStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlCreateServerStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateTableSpaceStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlCreateTableSpaceStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterEventStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterEventStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterLogFileGroupStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterServerStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterServerStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDatabasePartitionStatusStatement x) {
        return false;
    }

    @Override
    public void endVisit(MySqlShowDatabasePartitionStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlChecksumTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(MySqlChecksumTableStatement x) {

    }
}
