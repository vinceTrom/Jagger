package com.vtromeur.jagger.xmpp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.vtromeur.jagger.xmpp.Credentials;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by Vincent Tromeur on 08/12/15.
 */
public class ConnectAndLoginTask extends AsyncTask<Void, Void, Integer> {

    private XMPPTCPConnection mXMPPConnection;
    private Credentials mCredentials;
    private ConnectionStateListener mConnectionStateListener;

    public ConnectAndLoginTask(XMPPTCPConnection pConnection, Credentials pUserCredentials, ConnectionStateListener pConnectionListener) {
        mXMPPConnection = pConnection;
        mCredentials = pUserCredentials;
        mConnectionStateListener = pConnectionListener;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        String password = mCredentials.mPassword;
        String userName = mCredentials.mUsername;


        try {
                mXMPPConnection.connect();

            Log.i("XMPPClient", "[SettingsDialog] Connected to " + mXMPPConnection.getHost());
            Log.i("XMPPClient", "[SettingsDialog] Connected? " + mXMPPConnection.isConnected());
        } catch (SmackException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + mXMPPConnection.getHost());
            Log.e("XMPPClient", ex.toString());
            return ConnectionStateListener.NOT_CONNECTED;
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mXMPPConnection.isAuthenticated()) {
            sendPresenceAndFirstNameIQ();
            return ConnectionStateListener.AUTHENTICATED;
        }

        try {
            mXMPPConnection.login(userName, password);
            Log.i("XMPPClient", "Logged in as " + mXMPPConnection.getUser());
            sendPresenceAndFirstNameIQ();
            return ConnectionStateListener.AUTHENTICATED;
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + userName);
            Log.e("XMPPClient", ex.toString());

        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ConnectionStateListener.CONNECTED;
    }

    private void sendPresenceAndFirstNameIQ() {
        // Set the status to available
/*
        Presence presence = new Presence(Presence.Type.available);
        mXMPPConnection.sendPacket(presence);
        ProviderManager.getInstance().addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

                if(GCMManager.DEVICE_GCM_TOKEN != null){
					IQ iqPushToken = new TokenIQ(PLATFORM_ANDROID, GCMManager.DEVICE_GCM_TOKEN);
					String iq = iqPushToken.toXML();
					Log.e("", "iqPushToken: "+iq);
					mConnection.sendPacket(iqPushToken);
				}

        IQ nameToken = new FirstNameIQ(mCredentials.mUserAlias);
        mXMPPConnection.sendPacket(nameToken);
        */
    }

    @Override
    protected void onPostExecute(Integer connectionStatus) {
        if (mConnectionStateListener != null)
            switch (connectionStatus) {
                case ConnectionStateListener.NOT_CONNECTED:
                    mConnectionStateListener.connectionFailed();
                    break;
                case ConnectionStateListener.CONNECTED:
                    mConnectionStateListener.loggingFailed();
                    break;
                case ConnectionStateListener.AUTHENTICATED:
                    mConnectionStateListener.connectedAndLogged();
                    break;
            }
    }
}
