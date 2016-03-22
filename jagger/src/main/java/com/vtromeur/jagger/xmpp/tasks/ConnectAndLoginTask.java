package com.vtromeur.jagger.xmpp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.vtromeur.jagger.xmpp.Credentials;
import com.vtromeur.jagger.xmpp.iq.FirstNameIQ;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.DelayInformationProvider;

/**
 * Created by Vincent Tromeur on 08/12/15.
 */
public class ConnectAndLoginTask extends AsyncTask<Void, Void, Integer> {

    private XMPPConnection mXMPPConnection;
    private Credentials mCredentials;
    private ConnectionStateListener mConnectionStateListener;

    public ConnectAndLoginTask(XMPPConnection pConnection, Credentials pUserCredentials, ConnectionStateListener pConnectionListener) {
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
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + mXMPPConnection.getHost());
            Log.e("XMPPClient", ex.toString());
            return ConnectionStateListener.NOT_CONNECTED;
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
            return ConnectionStateListener.CONNECTED;
        }

    }

    private void sendPresenceAndFirstNameIQ() {
        // Set the status to available

        Presence presence = new Presence(Presence.Type.available);
        mXMPPConnection.sendPacket(presence);
        ProviderManager.getInstance().addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
/*
                if(GCMManager.DEVICE_GCM_TOKEN != null){
					IQ iqPushToken = new TokenIQ(PLATFORM_ANDROID, GCMManager.DEVICE_GCM_TOKEN);
					String iq = iqPushToken.toXML();
					Log.e("", "iqPushToken: "+iq);
					mConnection.sendPacket(iqPushToken);
				}
*/
        IQ nameToken = new FirstNameIQ("moi");
        String iq = nameToken.toXML();
        Log.e("", "nameToken: " + iq);
        mXMPPConnection.sendPacket(nameToken);
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
