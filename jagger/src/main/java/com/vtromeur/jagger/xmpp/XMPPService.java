package com.vtromeur.jagger.xmpp;

import android.content.Context;
import android.util.Log;

import com.vtromeur.jagger.MessageDbHelper;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPAccountCreationListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;
import com.vtromeur.jagger.xmpp.tasks.ConnectAndLoginTask;
import com.vtromeur.jagger.xmpp.tasks.CreateAccountTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.HashMap;


public class XMPPService {


    private static XMPPService sService;

    private XMPPServerConfig mServerConfig;
    private Credentials mUserCredentials;

    private XMPPTCPConnection mConnection;
    private HashMap<String, Chat> mChatMap = new HashMap<>();
    private XMPPOnMessageReceivedListener mMessageReceiverlistener;


    public XMPPService() {
    }


    public void init(Context ctx, XMPPServerConfig pServerConfig) {
        // Create a connection
        XMPPTCPConnectionConfiguration connConfig = createConnectionConfiguration(pServerConfig);
        mConnection = new XMPPTCPConnection(connConfig);
    }

    private XMPPTCPConnectionConfiguration createConnectionConfiguration(XMPPServerConfig pServerConfig) {
        mServerConfig = pServerConfig;

        XMPPTCPConnectionConfiguration.Builder connecConfigBuilder = XMPPTCPConnectionConfiguration.builder();
        connecConfigBuilder.setHost(pServerConfig.getHost());
        connecConfigBuilder.setPort(Integer.valueOf(pServerConfig.getPort()));
        connecConfigBuilder.setServiceName(pServerConfig.getHost());

        XMPPTCPConnectionConfiguration connecConfig = connecConfigBuilder.build();
        return connecConfig;
    }

    public void disconnect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mConnection.disconnect();
            }
        }).start();
    }


    public void connect(String userName, String password, ConnectionStateListener connectionListener) {
        connect(userName, password, userName, connectionListener);
    }

    public void connect(String userName, String password, String userAlias, ConnectionStateListener connectionListener) {
        mUserCredentials = new Credentials();
        mUserCredentials.mUsername = userName;
        mUserCredentials.mPassword = password;
        mUserCredentials.mUserAlias = userAlias;

        launchConnectAndLoginTask(connectionListener);
    }

    private void launchConnectAndLoginTask(final ConnectionStateListener connectionListener) {
        if (mConnection.isConnected())
            return;

        new ConnectAndLoginTask(mConnection, mUserCredentials, new ConnectionStateListener() {
            @Override
            public void connectionFailed() {
                connectionListener.connectionFailed();
            }

            @Override
            public void loggingFailed() {
                Log.i("XMPPClient", "LoggingFailed, will try to create an account");
                new CreateAccountTask(mConnection, mUserCredentials, new XMPPAccountCreationListener() {
                    @Override
                    public void accountCreated() {
                        Log.i("XMPPClient", "Account created, will try to connect now");
                        launchConnectAndLoginTask(connectionListener);
                    }

                    @Override
                    public void accountCreationFailed() {
                        Log.i("XMPPClient", "Account creation failed");
                        connectionListener.connectionFailed();
                    }
                }).execute();
            }

            @Override
            public void connectedAndLogged() {
                connectionListener.connectedAndLogged();
            }
        }).execute();
    }


    public void setMessageReceiver(String pChatterName, final XMPPOnMessageReceivedListener listener) {
        mMessageReceiverlistener = listener;

        ChatMessageListener messageListener = new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if(message.getBody() == null){
                    return;
                }
                XMPPMessage xmppMessage = new XMPPMessage(message.getFrom(), message.getTo(), message.getBody(), System.currentTimeMillis(), true);
                mMessageReceiverlistener.messageReceived(xmppMessage);
                MessageDbHelper.saveMessageInDB(xmppMessage);
            }
        };
        Chat chat = ChatManager.getInstanceFor(mConnection).createChat(pChatterName+"@"+mServerConfig.getUsernameSuffix(), messageListener);
        mChatMap.put(pChatterName, chat);
    }

    public void sendMessage(String pChatterName, String pMessage, final MessageSendingListener callback) {

        String userName = mUserCredentials.mUsername;
        if (mServerConfig.isMustAppendUsernameSuffix()) {
            userName = userName + "@" + mServerConfig.getUsernameSuffix();
        }

        String chatterName = pChatterName;
        if (mServerConfig.isMustAppendUsernameSuffix()) {
            chatterName = chatterName + "@" + mServerConfig.getUsernameSuffix();
        }

        Chat chat = mChatMap.get(pChatterName);
        XMPPMessage xMPPMessage = new XMPPMessage(userName, chatterName, pMessage, System.currentTimeMillis(), false);
        try {
            chat.sendMessage(xMPPMessage.getMessage());
            MessageDbHelper.saveMessageInDB(xMPPMessage);
            callback.messageSent(xMPPMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            callback.messageNotSent(xMPPMessage);
        }
    }

    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    public boolean isAuthenticated() {
        return mConnection != null && mConnection.isAuthenticated();
    }

}


