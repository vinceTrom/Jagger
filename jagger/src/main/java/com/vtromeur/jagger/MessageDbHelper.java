package com.vtromeur.jagger;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vtromeur.jagger.db.DatabaseHelper;
import com.vtromeur.jagger.xmpp.XMPPMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vince on 06/04/16.
 */
public class MessageDbHelper {

    public static boolean saveMessageInDB(XMPPMessage pMessage) {
        try {
            DatabaseHelper.getHelper().getMessageDao().create(parseBareID(pMessage));
            Log.e("aa", "mess sender:"+parseBareID(pMessage).getSenderId());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<XMPPMessage> getLastMessages(String pUserId, String pChatterId){
        try {
            Dao<XMPPMessage, Integer> messageDao = DatabaseHelper.getHelper().getMessageDao();

            QueryBuilder queryBuilder = messageDao.queryBuilder();
            Where where = queryBuilder.where();
            where.eq(XMPPMessage.SENDER_ID, pChatterId)
                    .and().eq(XMPPMessage.RECEIVER_ID, pUserId);
            where.eq(XMPPMessage.SENDER_ID, pUserId)
                    .and().eq(XMPPMessage.RECEIVER_ID, pChatterId);
            where.or(2);
            queryBuilder.limit(100L);
            queryBuilder.orderBy(XMPPMessage.DATE, true);

            PreparedQuery query = queryBuilder.prepare();
            return messageDao.query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static XMPPMessage parseBareID(XMPPMessage pMessage){
        XMPPMessage message = new XMPPMessage(parseBareID(pMessage.getSenderId()), parseBareID(pMessage.getReceiverId()),
                pMessage.getMessage(), pMessage.getDate().getTime(), pMessage.isReceived());
        return message;
    }

    private static String parseBareID(String pUserId){
        if(pUserId.contains("@")){
            int index = pUserId.indexOf("@");
            return pUserId.substring(0, index);
        }else{
            return pUserId;
        }
    }
}
