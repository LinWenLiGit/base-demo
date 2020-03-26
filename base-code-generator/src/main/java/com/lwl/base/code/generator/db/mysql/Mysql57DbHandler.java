package com.lwl.base.code.generator.db.mysql;
import	java.util.Arrays;

import com.lwl.base.code.generator.db.DbHandler;
import com.lwl.base.code.generator.db.DbHelper;
import com.lwl.base.code.generator.model.ColumnBean;
import com.lwl.base.code.generator.model.EntityBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mysql数据库处理器类
 * @author Admin
 */
public class Mysql57DbHandler implements DbHandler {
    private static final String SHOW_ALL_TABLE_NAME_SQL = "show tables";
    private static final String SHOW_TABLE_STATUS_SQL = "show table status where name='?'";
    private static final String SHOW_ALL_FIELD_SQL = "show full fields from ?";
    private DbHelper dbHelper = new DbHelper();

    public Mysql57DbHandler() {
    }

    @Override
    public List<EntityBean> tableListByName(String... tableName) {
        List<String> tableNames;
        if (tableName == null || tableName.length == 0) {
            tableNames = dbHelper.queryTableNameList(SHOW_ALL_TABLE_NAME_SQL);
        }else {
            tableNames = Arrays.asList(tableName);
        }
        // 分别根据表名获取表的表信息对象
        List<EntityBean> entityBeans = new ArrayList<>();
        for (String name : tableNames) {
            entityBeans.add(byTableName(name));
        }
        return entityBeans;
    }

    @Override
    public EntityBean byTableName(String tableName) {
        // 获取表状态信息
        String showTableSql = SHOW_TABLE_STATUS_SQL.replaceAll("\\?", tableName);
        List<Map<String, String>> tableInfos = dbHelper.queryTableInfoByTableName(showTableSql, new String[]{"Name", "Comment"});
        Map<String, String> tableInfo = tableInfos.get(0);
        MysqlTable dbTable = new MysqlTable(tableInfo.get("Name"), tableInfo.get("Comment"));
        EntityBean entityBean = dbTable.convert();
        // 获取表的列信息
        String showFieldSql = SHOW_ALL_FIELD_SQL.replaceAll("\\?", tableName);
        List<Map<String, String>> fields = dbHelper.queryTableInfoByTableName(showFieldSql, null);
        // 列信息转Java对象
        List<ColumnBean> columnBeans = new ArrayList<>();
        List<String> imports = new ArrayList<>();
        ColumnBean columnBean;
        for (Map<String, String> field : fields) {
            MysqlColumn dbField = new MysqlColumn(field.get("Field"), field.get("Type"), Boolean.parseBoolean(field.get("Null")), field.get("Comment"));
            columnBean = dbField.convert();
            columnBeans.add(columnBean);
            if (!imports.contains(columnBean.getTypeImport())) {
                imports.add(columnBean.getTypeImport());
            }
        }
        entityBean.setImports(imports);
        entityBean.setColumns(columnBeans);
        return entityBean;
    }

    @Override
    public void close() {
        dbHelper.closeAll();
    }
}
