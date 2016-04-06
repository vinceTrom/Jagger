package com.vtromeur.jagger.xmpp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

@DatabaseTable(tableName = "messages")
public class XMPPMessage {

    public static final String SENDER_ID = "SENDER_ID";
    public static final String RECEIVER_ID = "RECEIVER_ID";
    public static final String MESSAGE_TXT = "MESSAGE_TXT";
    public static final String DATE = "DATE";
    public static final String IS_RECEIVED = "IS_RECEIVED";


    private static SimpleDateFormat sDefaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SimpleDateFormat sTodayDateFormat = new SimpleDateFormat("HH:mm");

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = SENDER_ID)
    private String mSenderId;

    @DatabaseField(columnName = RECEIVER_ID)
    private String mReceiverId;

    @DatabaseField(columnName = MESSAGE_TXT)
    private String mMessageText;

    @DatabaseField(columnName = DATE)
    private Date mDate;

    @DatabaseField(columnName = IS_RECEIVED)
    private boolean mIsReceived;

    public XMPPMessage(){}

    public XMPPMessage(String pSenderId, String pReceiverId, String pMessageText, long pDate, boolean pIsReceived) {
        mSenderId = pSenderId;
        mReceiverId = pReceiverId;
        mMessageText = pMessageText;
        mDate = new Date(pDate);
        mIsReceived = pIsReceived;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getReceiverId() {
        return mReceiverId;
    }

    public void setSenderId(String pSenderId) {
        mSenderId = pSenderId;
    }

    public void setReceiverId(String pReceiverId) {
        mReceiverId = pReceiverId;
    }

    public String getMessage() {
        return mMessageText;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isReceived(){
        return mIsReceived;
    }

    public boolean isSent(){
        return !mIsReceived;
    }

    public static String getDateString(Date pDate) {
        Date today = new Date();
        if (pDate.getYear() == today.getYear() && pDate.getMonth() == today.getMonth() && pDate.getDate() == today.getDate())
            return sTodayDateFormat.format(pDate);
        else
            return sDefaultDateFormat.format(pDate);
    }

}
