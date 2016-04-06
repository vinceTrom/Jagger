package com.vtromeur.jagger.xmpp;

import android.os.Handler;
import android.util.Log;

import com.vtromeur.jagger.db.DatabaseHelper;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import java.sql.SQLException;

/**
 * Created by Vincent Tromeur on 10/12/15.
 */
public class MessageReceptionListener implements PacketListener {

    private XMPPOnMessageReceivedListener mMessageReceiver;
    private Handler mHandler;

    public MessageReceptionListener(XMPPOnMessageReceivedListener pMessageReceiver) {
        mMessageReceiver = pMessageReceiver;
        mHandler = new Handler();
    }

    @Override
    public void processPacket(Stanza pPacket) throws SmackException.NotConnectedException {
        Log.e("", "processPacket: " + pPacket.toXML());
        Message message = (Message) pPacket;
        long messageTimeStamp = 0;
        try {
            //DelayInformation inf = (DelayInformation) pPacket.getExtension("x", "jabber:x:delay");
            //messageTimeStamp = inf.getStamp().getTime();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (messageTimeStamp == 0)
            messageTimeStamp = System.currentTimeMillis();
        if (message.getBody() != null) {
            final XMPPMessage XMPPmessage = new XMPPMessage(message.getFrom(), message.getTo(), message.getBody(), messageTimeStamp, true);

            saveMessageInDB(XMPPmessage);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMessageReceiver.messageReceived(XMPPmessage);
                }
            });
        }
    }

    private void saveMessageInDB(XMPPMessage pMessage) {
        try {
            DatabaseHelper.getHelper().getMessageDao().create(pMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
