package com.vtromeur.jagger.xmpp;

import org.jivesoftware.smack.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XMPPMessage {

    private static SimpleDateFormat sDefaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SimpleDateFormat sTodayDateFormat = new SimpleDateFormat("HH:mm");

    private String mSenderId;
    private String mReceiverId;
    private String mMessageText;
    private Date mDate;
    private boolean mIsReceived;

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

    public String getBareSenderId() {
        return  StringUtils.parseBareAddress(mSenderId);
    }

    public String getBareReceiverId() {
        return  StringUtils.parseBareAddress(mReceiverId);
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
