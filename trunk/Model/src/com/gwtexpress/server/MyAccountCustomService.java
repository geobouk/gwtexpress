package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.SerializableException;

import com.gwtexpress.client.rpc.model.MyAccountMetaData;
import com.gwtexpress.server.util.RPCExceptionUtil;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;


public class MyAccountCustomService implements GLogixCustomService {
    DBMetaDataObject dbMetaDataObject;

    public MyAccountCustomService() {
    }

    public void init(DBMetaDataObject dbMetaDataObject) {
        this.dbMetaDataObject = dbMetaDataObject;
    }

    public ArrayList<String[]> customPostService(ArrayList<String[]> data) throws SerializableException {
        if (data == null || data.size() == 0)
            return null;
        MyAccountMetaData metaData = new MyAccountMetaData();
        String[] row = data.get(0);

        UserSession session = dbMetaDataObject.getSession();
        StringBuffer sb = new StringBuffer();
        PreparedStatement ps = null;

        String fn = row[metaData.getColumnIndex("FIRST_NAME")];
        String ln = row[metaData.getColumnIndex("LAST_NAME")];
        String dn = row[metaData.getColumnIndex("DISPLAY_NAME")];
        String op = row[metaData.getColumnIndex("OLD_PASSWORD")];
        String pw1 = row[metaData.getColumnIndex("NEW_PASSWORD1")];
        String pw2 = row[metaData.getColumnIndex("NEW_PASSWORD2")];
        boolean pwchange = true;
        if (("".equals(op) || op == null) && ("".equals(pw1) || pw1 == null) && ("".equals(pw2) || pw2 == null)) {
            pwchange = false;
        }
        if (pwchange) {
            if (pw1 == null || op == null || pw2 == null) {
                throw RPCExceptionUtil.sendErrorToClient("Missing password.");
            }
            if (op.equals(pw1)) {
                throw RPCExceptionUtil.sendErrorToClient("New password & Old password are same.");
            }
            if (!pw1.equals(pw2)) {
                throw RPCExceptionUtil.sendErrorToClient("New passwords doesn't match.");
            }
            boolean validPwd = BCrypt.checkpw(op, session.getPasswordHash());
            if (!validPwd) {
                throw RPCExceptionUtil.sendErrorToClient("Invalid password.");
            }
        }
        try {

            sb.append("UPDATE user_accounts SET FIRST_NAME = ?,LAST_NAME = ?, DISPLAY_NAME = ?,LAST_UPDATE_DATE =?");
            if (pwchange)
                sb.append(", PASSWORD_HASH =?, LAST_PASSWORD_CHANGED = ?");
            sb.append(" WHERE USER_ID = ?");
            ps = 
 dbMetaDataObject.getConnection().prepareStatement(sb.toString());


            Timestamp sysdate = 
                new Timestamp(Calendar.getInstance().getTimeInMillis());
            int bind = 1;
            ps.setString(bind++, fn);
            ps.setString(bind++, ln);
            ps.setString(bind++, dn);
            ps.setTimestamp(bind++, sysdate);
            if (pwchange) {
                ps.setString(bind++, SessionManager.getHashValue(pw1));
                ps.setTimestamp(bind++, sysdate);
            }
            ps.setBigDecimal(bind++, session.getUserId());
            int resultRows = ps.executeUpdate();
            if (resultRows == 0) {
                throw RPCExceptionUtil.sendErrorToClient("ERROR: 0 rows updated!!!");
            }
            dbMetaDataObject.getConnection().commit();
            try {
                if (ps != null)
                    ps.close();
            } catch (Throwable e) {
            }
            return data;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Error posting data", e);
        }
    }

    public ArrayList<String[]> customFindService(String[] params) {
        return null;
    }
}
