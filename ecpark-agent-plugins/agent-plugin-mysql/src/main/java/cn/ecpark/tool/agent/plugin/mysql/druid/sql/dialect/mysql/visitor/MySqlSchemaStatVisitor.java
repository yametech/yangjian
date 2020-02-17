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
package cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.visitor;

import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLName;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.SQLObject;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLAlterCharacter;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLAlterTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLCreateTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLExprTableSource;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLForeignKeyImpl;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectOrderByItem;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectQueryBlock;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLSelectStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLStartTransactionStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLTableSource;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.ast.statement.SQLUpdateStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlKey;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlUnique;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterEventStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterLogFileGroupStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterServerStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterTablespaceStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlChecksumTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateAddLogFileGroupStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateEventStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateServerStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableSpaceStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlEventSchedule;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasePartitionStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import cn.ecpark.tool.agent.plugin.mysql.druid.sql.visitor.SchemaStatVisitor;
import cn.ecpark.tool.agent.plugin.mysql.druid.stat.TableStat;
import cn.ecpark.tool.agent.plugin.mysql.druid.stat.TableStat.Mode;
import cn.ecpark.tool.agent.plugin.mysql.druid.util.JdbcConstants;
import cn.ecpark.tool.agent.plugin.mysql.druid.util.JdbcUtils;

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
