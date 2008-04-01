package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.SerializableException;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.dbpool.db.ConnectionPool;
import com.gwtexpress.dbpool.db.ConnectionPoolManager;
import com.gwtexpress.server.util.RPCExceptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DBMetaDataObject {
    static char COMMA = ',';
    static char BIND = '?';
    static char SPACE = ' ';
    static char EQUALS = '=';
    private Connection con;
    private boolean txnStarted;
    private static ConnectionPool connectionPool;
    UserSession session;

    public DBMetaDataObject() {
    }

    static {
        try {
            connectionPool = ConnectionPoolManager.getInstance().getPool("gwtexpress");
        } catch (Throwable e) {
        }
    }

    public String getStatus() {
        return "Checked Out=" + connectionPool.getCheckedOut() + ", Free=" + 
            connectionPool.getFreeCount() + ", Max=" + 
            connectionPool.getMaxSize() + ", Size=" + 
            connectionPool.getSize() + ", Pool Size=" + 
            connectionPool.getPoolSize() + ", Hit Rate=" + 
            connectionPool.getHitRate();
    }

    public void startTxn() throws SQLException, SerializableException {
        if (txnStarted)
            return;
        if (connectionPool == null)
            throw RPCExceptionUtil.sendErrorToClient("Database Connect Pool not established. Please contact your system administrator.");
        try {
            con = connectionPool.getConnection();
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Unable to connect to the database. Please contact your system administrator.", 
                                                     e);
        }
        if (con == null)
            throw RPCExceptionUtil.sendErrorToClient("NULL Database Connection. Please contact your system administrator.");
        con.setAutoCommit(false);
        txnStarted = true;
    }

    public void commit() throws SQLException {
        if (txnStarted)
            con.commit();
    }

    public void rollback() throws SQLException {
        if (txnStarted)
            con.rollback();
    }

    public Connection getConnection() {
        if (txnStarted)
            return con;
        else
            return null;
    }

    public void endTxn() {
        if (con == null || !txnStarted)
            return;
        try {
            con.rollback();
        } catch (Throwable e) {
        }
        try {
            con.close();
        } catch (Throwable e) {
        }
        con = null;
        txnStarted = false;
    }
    StringBuffer sb = new StringBuffer();

    public String getSelectStmt(GLogixMetaData md) {
        return getSelectStmt(md, md.getSelectColumnNames(), 
                             md.getPkColumnNames());
    }

    public String getSelectStmt(GLogixMetaData md, String[] selectColumnNames, 
                                String[] whereColumnNames) {
        sb.setLength(0);
        sb.append("SELECT ");
        for (int i = 0; i < selectColumnNames.length; i++) {
            if (i > 0)
                sb.append(COMMA);
            sb.append(selectColumnNames[i]);
        }
        sb.append(" FROM ").append(md.getTableName());
        if (whereColumnNames != null && whereColumnNames.length > 0) {
            sb.append(" WHERE ");
            for (int i = 0; i < whereColumnNames.length; i++) {
                sb.append(whereColumnNames[i]).append(EQUALS).append(BIND);
            }
        }
        return sb.toString();
    }

    public String getInsertStmt(GLogixMetaData md) {
        sb.setLength(0);
        sb.append("INSERT INTO ").append(md.getTableName()).append(" (");
        String[] columnNames = md.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0)
                sb.append(COMMA);
            sb.append(columnNames[i]);
        }
        sb.append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0)
                sb.append(COMMA);
            sb.append(BIND);
        }
        sb.append(")");
        return sb.toString();
    }

    public String getUpdateStmt(GLogixMetaData metaData) {
        sb.setLength(0);
        String[] columnNames = metaData.getColumnNames();
        boolean[] downloadableCols = metaData.getDownloadable();
        String[] pkColumnNames = metaData.getPkColumnNames();
        sb.append("UPDATE ").append(metaData.getTableName()).append(" SET ");
        for (int i = 0; i < columnNames.length; i++) {
            if (downloadableCols == null || downloadableCols[i]) {
                if (i > 0)
                    sb.append(", ");
                sb.append(columnNames[i]).append(EQUALS).append(BIND);
            }
        }
        sb.append(" WHERE ");
        if (metaData.getRowIdIndex() >= 0) {
            sb.append(columnNames[metaData.getRowIdIndex()]).append(EQUALS).append(BIND);
        } else {
            for (int i = 0; i < pkColumnNames.length; i++) {
                sb.append(pkColumnNames[i]).append(EQUALS).append(BIND);
            }
        }
        return sb.toString();
    }

    public boolean isNew(GLogixMetaData md, String[] data) throws SQLException, 
                                                                  ParseException, 
                                                                  SerializableException {
        boolean isnew = false;
        if (md.getRowIdIndex() >= 0) {
            if (data[md.getRowIdIndex()] == null) {
                isnew = true;
            }
        } else {
            int[] pki = md.getPkColumnIndexs();
            String[] pkNames = md.getPkColumnNames();
            char[] columnTypes = md.getColumnTypes();
            sb.setLength(0);
            sb.append("SELECT 1 FROM ").append(md.getTableName()).append(" WHERE ");
            for (int i = 0; i < pki.length; i++) {
                if (data[pki[i]] == null) {
                    throw RPCExceptionUtil.sendErrorToClient("Missing value for primary key column " + 
                                                             pkNames[i]);
                }
                sb.append(pkNames[i]).append(EQUALS).append(BIND);
            }
            PreparedStatement ps = con.prepareStatement(sb.toString());
            for (int i = 1; i <= pkNames.length; i++) {
                ps.setObject(i, 
                             decodeData('Q', pkNames[i - 1], columnTypes[i - 1], 
                                        data[i - 1]));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isnew = false;
            } else {
                isnew = true;
            }
            rs.close();
            ps.close();

        }
        return isnew;
    }

    public static int getResultSetSize(ResultSet resultSet) {
        int size = -1;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (SQLException e) {
            return size;
        }
        return size;
    }

    public Object decodeData(char mode, String colName, char columnType, 
                             String value) throws ParseException {

        if (columnType == 'D') {
            if ((mode == 'I' && 
                 "CREATION_DATE".equals(colName.toUpperCase())) || 
                (mode != 'Q' && 
                 "LAST_UPDATE_DATE".equals(colName.toUpperCase()))) {
                return new Date(Calendar.getInstance().getTimeInMillis());
            }
        } else if (columnType == 'T') {
            if ((mode == 'I' && 
                 "CREATION_DATE".equals(colName.toUpperCase())) || 
                (mode != 'Q' && 
                 "LAST_UPDATE_DATE".equals(colName.toUpperCase()))) {
                return new Date(Calendar.getInstance().getTimeInMillis());
            }
        } else if (columnType == 'N' && 
                   (mode == 'I' && "CREATED_BY".equals(colName.toUpperCase())) || 
                   (mode != 'Q' && 
                    "LAST_UPDATED_BY".equals(colName.toUpperCase()))) {
            return session.getUserId();
        }

        if (value == null || value.length() == 0)
            return null;
        Object obj = value;
        if (columnType == 'D') {
            obj = new Date(Long.parseLong(value));
        } else if (columnType == 'T') {
            obj = new Timestamp(Long.parseLong(value));
        }
        return obj;
    }

    public String[] save(GLogixMetaData metaData, 
                         String[] row) throws SQLException, ParseException, 
                                              SerializableException {
        String[] columnNames = metaData.getColumnNames();
        char[] columnTypes = metaData.getColumnTypes();
        if (isNew(metaData, row)) {
            String sql;
            sql = getInsertStmt(metaData);
            PreparedStatement insertPS = con.prepareStatement(sql);
            for (int i = 1; i <= columnNames.length; i++) {
                insertPS.setObject(i, 
                                   decodeData('I', columnNames[i - 1], columnTypes[i - 
                                              1], row[i - 1]));
            }
            insertPS.executeUpdate();
            ResultSet rs = insertPS.getGeneratedKeys();
            while (rs.next()) {
                row[metaData.getRowIdIndex()] = rs.getObject(1).toString();
            }
            rs.close();
            insertPS.close();
        } else {
            PreparedStatement updatePS = 
                con.prepareStatement(getUpdateStmt(metaData));
            boolean[] downloadableCols = metaData.getDownloadable();
            int bind = 0;
            for (int i = 1; i <= columnNames.length; i++) {
                if (downloadableCols == null || downloadableCols[i - 1]) {
                    bind++;
                    updatePS.setObject(bind, 
                                       decodeData('U', columnNames[i - 1], 
                                                  columnTypes[i - 1], 
                                                  row[i - 1]));
                }
            }
            if (metaData.getRowIdIndex() >= 0) {
                bind++;
                updatePS.setObject(bind, row[metaData.getRowIdIndex()]);
            } else {
                String[] pkColumnNames = metaData.getPkColumnNames();
                for (int i = 1; i <= pkColumnNames.length; i++) {
                    bind++;
                    updatePS.setObject(bind, row[i - 1]);
                }
            }
            int i = updatePS.executeUpdate();
            if (i == 0) {
                throw RPCExceptionUtil.sendErrorToClient("Warning: No changes made during this post...");
            }
            updatePS.close();
        }
        return row;
    }

    private String getColumnLabel(String attrName) {
        String val = attrName.replaceAll("_", " ");
        return initCap(val);
    }

    private String initCap(String in) {
        boolean capitalize = true;
        char[] data = in.toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ' ' || Character.isWhitespace(data[i]))
                capitalize = true;
            else if (capitalize) {
                data[i] = Character.toUpperCase(data[i]);
                capitalize = false;
            } else
                data[i] = Character.toLowerCase(data[i]);
        }
        return new String(data);
    }

    public String dbToJavaName(String colName) {
        colName = getColumnLabel(colName);
        colName = colName.replaceAll(" ", "");
        colName = colName.substring(0, 1).toLowerCase() + colName.substring(1);
        return colName;
    }

    private StringBuffer appendSelectClause(GLogixMetaData md, 
                                            StringBuffer sb) {
        String[] selectColumnNames = md.getSelectColumnNames();
        boolean[] dwonloadableCols = md.getDownloadable();
        sb.append("SELECT ");
        for (int i = 0; i < selectColumnNames.length; i++) {
            if (i > 0)
                sb.append(COMMA);
            if (dwonloadableCols == null || dwonloadableCols[i])
                sb.append(selectColumnNames[i]);
            else
                sb.append("null");
        }
        sb.append(" FROM ").append(md.getTableName());
        return sb;
    }

    public MetaDataObject getMetaDataObject(GLogixMetaData metaData, 
                                            List<String[]> paramsList, 
                                            String andOr) throws SQLException, 
                                                                 ParseException {
        sb.setLength(0);
        String[] colNames = metaData.getColumnNames();
        char[] colType = metaData.getColumnTypes();
        appendSelectClause(metaData, sb);
        int count = 0;
        Object[] binds = null;
        String[] params;
        String cond;
        if (paramsList != null) {
            for (int i = 0; i < paramsList.size(); i++) {
                params = paramsList.get(i);
                if (params[2] != null && params[2].trim().length() > 0) {
                    int colIdx = Integer.parseInt(params[0]);
                    sb.append(SPACE);
                    if (count == 0)
                        sb.append("WHERE");
                    else
                        sb.append(" ").append(andOr).append(" ");
                    sb.append(SPACE);
                    count++;
                    if ("C".equals(params[1])) {
                        cond = "LIKE";
                        params[2] = "%" + params[2] + "%";
                    } else if ("SW".equals(params[1])) {
                        cond = "LIKE";
                        params[2] = params[2] + "%";
                    } else if ("EW".equals(params[1])) {
                        cond = "LIKE";
                        params[2] = "%" + params[2];
                    } else {
                        cond = params[1];
                    }
                    sb.append(colNames[colIdx]).append(SPACE).append(cond).append(" ? ");
                } else {
                    paramsList.remove(i--);
                }
            }
            binds = new String[count];
            count = 0;
            for (int i = 0; i < paramsList.size(); i++) {
                params = paramsList.get(i);
                if (params[2] != null && params[2].trim().length() > 0) {
                    binds[count++] = params[2];
                }
            }
        }
        String[] orderByColNames = metaData.getOrderByColumnNames();
        if (orderByColNames != null && orderByColNames.length > 0) {
            sb.append(" ORDER BY ");
            for (int i = 0; i < orderByColNames.length; i++) {
                if (i > 0)
                    sb.append(COMMA);

                sb.append(orderByColNames[i]);
            }
        }
        sb.append(" LIMIT 100");
        return getMetaDataObject(metaData, sb.toString(), paramsList);
    }

    public MetaDataObject getMetaDataObject(GLogixMetaData metaData, 
                                            String sql, 
                                            List<String[]> paramList) throws SQLException, 
                                                                             ParseException {
        PreparedStatement ps = con.prepareStatement(sql);
        char[] colType = metaData.getColumnTypes();
        String[] colNames = metaData.getColumnNames();
        int colIdx;
        if (paramList != null) {
            String[] params;
            for (int i = 1; i <= paramList.size(); i++) {
                params = paramList.get(i - 1);
                colIdx = Integer.parseInt(params[0]);
                ps.setObject(i, 
                             decodeData('Q', colNames[colIdx], colType[colIdx], 
                                        params[2]));
            }
        }
        ResultSet rs = ps.executeQuery();
        MetaDataObject md = createFromResultSet(rs);
        try {
            if (rs != null)
                rs.close();
        } catch (Throwable e) {
        }
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
        return md;
    }

    public MetaDataObject getMetaDataObject(GLogixMetaData metaData, 
                                            String[] params) throws SQLException {
        sb.setLength(0);
        String[] colNames = metaData.getColumnNames();
        appendSelectClause(metaData, sb);
        int count = 0;
        Object[] binds = null;

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null && params[i].trim().length() > 0) {
                    if (count == 0)
                        sb.append(" WHERE ");
                    else
                        sb.append(" AND ");
                    count++;
                    sb.append(colNames[i]).append(" LIKE ? ");
                }
            }
            binds = new String[count];
            count = 0;
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null && params[i].trim().length() > 0) {
                    binds[count++] = params[i];
                }
            }
        }
        String[] orderByColNames = metaData.getOrderByColumnNames();
        if (orderByColNames != null && orderByColNames.length > 0) {
            sb.append(" ORDER BY ");
            for (int i = 0; i < orderByColNames.length; i++) {
                if (i > 0)
                    sb.append(COMMA);

                sb.append(orderByColNames[i]);
            }
        }        
        if (!"LookupValues".equals(metaData.getServiceName()))
            sb.append(" LIMIT 100");
        return getMetaDataObject(sb.toString(), binds);
    }

    public MetaDataObject getMetaDataObject2(String sql, 
                                             Object[] params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (params != null) {
            for (int i = 1; i <= params.length; i++) {
                ps.setObject(i, params[i - 1]);
            }
        }
        ResultSet rs = ps.executeQuery();
        MetaDataObject md = createFromResultSet2(rs);
        try {
            if (rs != null)
                rs.close();
        } catch (Throwable e) {
        }
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
        return md;
    }

    public MetaDataObject getMetaDataObject(String sql, 
                                            Object[] params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (params != null) {
            for (int i = 1; i <= params.length; i++) {
                ps.setObject(i, params[i - 1]);
            }
        }
        ResultSet rs = ps.executeQuery();
        MetaDataObject md = createFromResultSet(rs);
        try {
            if (rs != null)
                rs.close();
        } catch (Throwable e) {
        }
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
        return md;
    }

    public MetaDataObject getMetaDataObject(String sql) throws SQLException {
        return getMetaDataObject(sql, new String[] { });
    }

    public MetaDataObject getMetaDataObject2(String sql) throws SQLException {
        return getMetaDataObject2(sql, new String[] { });
    }

    public MetaDataObject createFromResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int totalColumns = resultSetMetaData.getColumnCount();
        MetaDataObject metaDataObject = new MetaDataObject("test");
        String[] row;
        while (rs.next()) {
            row = new String[totalColumns];
            for (int i = 1; i <= totalColumns; i++) {
                Object o = rs.getObject(i);
                if (o != null) {
                    if (o instanceof Timestamp) {
                        row[i - 1] = ((Timestamp)o).getTime() + "";
                    } else if (o instanceof Date) {
                        row[i - 1] = ((Date)o).getTime() + "";

                    } else if (o instanceof String) {
                        row[i - 1] = ((String)o);

                    } else {
                        row[i - 1] = o.toString();
                    }
                }
            }
            metaDataObject.addRow(row);
        }
        return metaDataObject;
    }


    public MetaDataObject createFromResultSet2(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int totalColumns = resultSetMetaData.getColumnCount();
        String[] columnNames = new String[totalColumns];
        String[] columnTitles = new String[totalColumns];
        char[] columnTypes = new char[totalColumns];
        int[] columnSizes = new int[totalColumns];
        boolean[] required = new boolean[totalColumns];
        MetaDataObject metaDataObject = 
            new MetaDataObject("test", columnNames, columnTitles, columnTypes, 
                               columnSizes);
        metaDataObject.required = required;
        for (int i = 1; i <= totalColumns; i++) {
            columnNames[i - 1] = resultSetMetaData.getColumnName(i);
            columnSizes[i - 1] = resultSetMetaData.getColumnDisplaySize(i);
            columnTitles[i - 1] = 
                    getColumnLabel(resultSetMetaData.getColumnLabel(i));
            if (ResultSetMetaData.columnNullable == 
                resultSetMetaData.isNullable(i)) {
                required[i - 1] = false;
            } else {
                required[i - 1] = true;
            }
            int ct = resultSetMetaData.getColumnType(i);
            switch (ct) {
            case Types.BIGINT:
            case Types.NUMERIC:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
                columnTypes[i - 1] = 'N';
                break;
            case Types.DATE:
                columnTypes[i - 1] = 'D';
                break;
            case Types.TIMESTAMP:
                columnTypes[i - 1] = 'T';
                break;
            default:
                columnTypes[i - 1] = 'V';
            }
        }
        String[] row;
        while (rs.next()) {
            row = new String[totalColumns];
            for (int i = 1; i <= totalColumns; i++) {
                if (columnTypes[i - 1] == 'D') {
                    Object o = rs.getObject(i);
                    if (o != null) {
                        row[i - 1] = rs.getDate(i).getTime() + "";
                    }
                } else if (columnTypes[i - 1] == 'T') {
                    Object o = rs.getObject(i);
                    if (o != null) {
                        row[i - 1] = rs.getTimestamp(i).getTime() + "";
                    }
                } else {
                    row[i - 1] = rs.getString(i);
                }
            }
            metaDataObject.addRow(row);
        }
        return metaDataObject;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public UserSession getSession() {
        return session;
    }
}
