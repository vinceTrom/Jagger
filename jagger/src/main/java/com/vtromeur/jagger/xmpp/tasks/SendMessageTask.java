package com.vtromeur.jagger.xmpp.tasks;

import android.os.AsyncTask;

import com.vtromeur.jagger.DatabaseHelper;
import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;

/**
 * Created by Vincent Tromeur on 08/12/15.
 */
public class SendMessageTask extends AsyncTask<Void, Void, Integer> {


    private XMPPConnection mXMPPConnection;
    private XMPPMessage mMessage;
    private MessageSendingListener mMessageSendingListener;

    public SendMessageTask(XMPPConnection pConnection, XMPPMessage pMessage, MessageSendingListener pListener) {
        mXMPPConnection = pConnection;
        mMessage = pMessage;
        mMessageSendingListener = pListener;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        final Message msg = new Message(mMessage.getReceiverId(), Message.Type.chat);
        msg.setBody(mMessage.getMessage());
        msg.setFrom(mMessage.getSenderId());
        //msg.addExtension(PacketExtensionProvider);
        if (mXMPPConnection.isConnected() && mXMPPConnection.isAuthenticated()) {
            try {
                mXMPPConnection.sendPacket(msg);
                saveMessageInDB();
                return MessageSendingListener.MESSAGE_SENT;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return MessageSendingListener.MESSAGE_NOT_SENT;
            }
        } else {
            throw new IllegalStateException("User must be connected and authenticated to be able to send a message");
        }
    }

    private void saveMessageInDB() {
        try {
            DatabaseHelper.getHelper().getMessageDao().create(mMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Integer messageSendStatus) {
        switch (messageSendStatus) {
            case MessageSendingListener.MESSAGE_SENT:
                mMessageSendingListener.messageSent(mMessage);
                break;
            case MessageSendingListener.MESSAGE_NOT_SENT:
                mMessageSendingListener.messageNotSent(mMessage);
                break;

        }
    }
}
