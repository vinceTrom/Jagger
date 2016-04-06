package com.vtromeur.jagger.xmpp.tasks;

import android.os.AsyncTask;

import com.vtromeur.jagger.xmpp.Credentials;
import com.vtromeur.jagger.xmpp.listeners.XMPPAccountCreationListener;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by Vincent Tromeur on 08/12/15.
 */
public class CreateAccountTask extends AsyncTask<Void, Void, Integer> {

    private XMPPConnection mXMPPConnection;
    private Credentials mCredentials;
    private XMPPAccountCreationListener mAccountCreationListener;

    public CreateAccountTask(XMPPConnection pConnection, Credentials pUserCredentials, XMPPAccountCreationListener pAccountCreationListener) {
        mXMPPConnection = pConnection;
        mCredentials = pUserCredentials;
        mAccountCreationListener = pAccountCreationListener;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        /*
        AccountManager accountMng = mXMPPConnection.getAccountManager();
        try {
            accountMng.createAccount(mCredentials.mUsername, mCredentials.mPassword);
            return XMPPAccountCreationListener.ACCOUNT_CREATED;
        } catch (Exception e) {
            e.printStackTrace();
            return XMPPAccountCreationListener.ACCOUNT_NOT_CREATED;
        }
        */
        return XMPPAccountCreationListener.ACCOUNT_NOT_CREATED;
    }

    @Override
    protected void onPostExecute(Integer accountCreationStatus) {
        if (mAccountCreationListener != null)
            switch (accountCreationStatus) {
                case XMPPAccountCreationListener.ACCOUNT_CREATED:
                    mAccountCreationListener.accountCreated();
                    break;
                case XMPPAccountCreationListener.ACCOUNT_NOT_CREATED:
                    mAccountCreationListener.accountCreationFailed();
                    break;

            }
    }

}
