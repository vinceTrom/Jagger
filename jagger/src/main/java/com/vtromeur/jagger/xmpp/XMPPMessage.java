package com.vtromeur.jagger.xmpp;

import org.jivesoftware.smack.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XMPPMessage {

    private static SimpleDateFormat sDefaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SimpleDateFormat sTodayDateFormat = new SimpleDateFormat("HH:mm");

    private String mSenderId;
    private String mReceiverId;
    private String mMessage;
    private Date mDate;

    public XMPPMessage(String senderId, String receiverId, String message, long date) {
        mSenderId = senderId;
        mReceiverId = receiverId;
        mMessage = message;
        mDate = new Date(date);
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
        return mMessage;
    }

    public Date getDate() {
        return mDate;
    }


    public static String getDateString(Date pDate) {
        Date today = new Date();
        if (pDate.getYear() == today.getYear() && pDate.getMonth() == today.getMonth() && pDate.getDate() == today.getDate())
            return sTodayDateFormat.format(pDate);
        else
            return sDefaultDateFormat.format(pDate);
    }

}
