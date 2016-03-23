package com.vtromeur.jagger.xmpp.tasks;

import android.os.AsyncTask;

import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

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
/*
    private void messageSent() {
        //XMPPMessagesCache.getInstance().saveMessage(message.getReceiverId(), message);
        //ChatSessions.getInstance().refreshChatSessionTimeStamp(message.getReceiverId(), System.currentTimeMillis());
        callback.messageSent();
    }
    private void messageNOTSent() {
        callback.messageNotSent();
    }
*/

    @Override
    protected Integer doInBackground(Void... params) {
        String dest = mMessage.getReceiverId();

        final Message msg = new Message(dest, Message.Type.chat);
        msg.setBody(mMessage.getMessage());
        msg.setFrom(mMessage.getSenderId());
        //msg.addExtension(PacketExtensionProvider);
        if (mXMPPConnection.isConnected() && mXMPPConnection.isAuthenticated()) {
            try {
                mXMPPConnection.sendPacket(msg);
                return MessageSendingListener.MESSAGE_SENT;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return MessageSendingListener.MESSAGE_NOT_SENT;
            }
        } else {
            throw new IllegalStateException("User must be connected and authenticated to be able to send a message");
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
