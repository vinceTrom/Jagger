package com.vtromeur.jagger.xmpp;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPAccountCreationListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;
import com.vtromeur.jagger.xmpp.tasks.ConnectAndLoginTask;
import com.vtromeur.jagger.xmpp.tasks.CreateAccountTask;
import com.vtromeur.jagger.xmpp.tasks.SendMessageTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;

import java.io.File;


public class XMPPService {


    private static XMPPService sService;

    private XMPPServerConfig mServerConfig;
    private Credentials mUserCredentials;

    private SmackAndroid mSmackInstance;
    private XMPPConnection mConnection;
    private PacketListener mMessageReceptionListener;
    private XMPPOnMessageReceivedListener mMessageReceiverlistener;


    private XMPPService() {
    }

    public static XMPPService getInstance() {
        if (sService == null)
            sService = new XMPPService();
        return sService;
    }

    public void init(Context ctx, XMPPServerConfig pServerConfig) {
        mSmackInstance = SmackAndroid.init(ctx);

        // Create a connection
        ConnectionConfiguration connConfig = createConnectionConfiguration(pServerConfig);
        mConnection = new XMPPConnection(connConfig);
    }

    private ConnectionConfiguration createConnectionConfiguration(XMPPServerConfig pServerConfig) {
        mServerConfig = pServerConfig;

        ConnectionConfiguration connConfig = new ConnectionConfiguration(pServerConfig.getHost(), Integer.parseInt(pServerConfig.getPort()));

        if (pServerConfig.isSASLAuthenticationEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                connConfig.setTruststoreType("AndroidCAStore");
                connConfig.setTruststorePassword(null);
                connConfig.setTruststorePath(null);
            } else {
                connConfig.setTruststoreType("BKS");
                String path = System.getProperty("javax.net.ssl.trustStore");
                if (path == null)
                    path = System.getProperty("java.home") + File.separator + "etc"
                            + File.separator + "security" + File.separator
                            + "cacerts.bks";
                connConfig.setTruststorePath(path);
            }
            connConfig.setSASLAuthenticationEnabled(true);
        }
        return connConfig;
    }

    public void disconnect() {
        try {
            mSmackInstance.onDestroy();
        } catch (Exception e) {
        }

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
                if (mMessageReceiverlistener != null) {
                    setMessageReceiver(mMessageReceiverlistener);
                }
                connectionListener.connectedAndLogged();
            }
        }).execute();
    }


    public void setMessageReceiver(final XMPPOnMessageReceivedListener listener) {
        mMessageReceiverlistener = listener;
        if (mMessageReceptionListener != null) {
            mConnection.removePacketListener(mMessageReceptionListener);
        }
        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        mMessageReceptionListener = new MessageReceptionListener(mMessageReceiverlistener);
        mConnection.addPacketListener(mMessageReceptionListener, filter);
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

        XMPPMessage xMPPMessage = new XMPPMessage(userName, chatterName, pMessage, System.currentTimeMillis(), false);
        sendMessage(xMPPMessage, callback);
    }

    public void sendMessage(XMPPMessage pXMPPMessage, final MessageSendingListener callback) {
        new SendMessageTask(mConnection, pXMPPMessage, callback).execute();
    }

    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    public boolean isAuthenticated() {
        return mConnection != null && mConnection.isAuthenticated();
    }

}


