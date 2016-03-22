package com.vtromeur.jagger.xmpp;

import org.jivesoftware.smack.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XMPPMessage {


    private String _senderId;
    private String _receiverId;
    private String _message;
    private Date _date;

    public XMPPMessage(String senderId, String receiverId, String message, long date) {
        _senderId = senderId;
        _receiverId = receiverId;
        _message = message;
        _date = new Date(date);
    }

    public String getSenderId() {
        return _senderId;
    }

    public String getReceiverId() {
        return _receiverId;
    }

    public String getBareSenderId() {
        return  StringUtils.parseBareAddress(_senderId);
    }

    public String getBareReceiverId() {
        return  StringUtils.parseBareAddress(_receiverId);
    }

    public String getMessage() {
        return _message;
    }

    public Date getDate() {
        return _date;
    }

    private SimpleDateFormat _dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat _todayDateFormat = new SimpleDateFormat("HH:mm");


    public String getDateString() {
        Date today = new Date();
        if (_date.getYear() == today.getYear() && _date.getMonth() == today.getMonth() && _date.getDate() == today.getDate())
            return _todayDateFormat.format(_date);
        else
            return _dateFormat.format(_date);
    }

}
