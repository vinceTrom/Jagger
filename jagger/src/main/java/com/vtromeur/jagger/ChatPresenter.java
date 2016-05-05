package com.vtromeur.jagger;

import android.content.Context;

import com.vtromeur.jagger.db.DatabaseHelper;
import com.vtromeur.jagger.di.DaggerSingleton;
import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;
import com.vtromeur.jagger.xmpp.XMPPService;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Vincent Tromeur on 05/05/16.
 */
public class ChatPresenter {

    private String mUserName;
    private String mPassword;
    private String mChatterName;

    private XMPPServerConfig mServerConfig;

    @Inject
    public XMPPService mXmppService;

    @Inject
    public Context mContext;

    @Inject
    public MessageDbHelper mMessageDB;

    private ChatFragment mChatView;


    public ChatPresenter(ChatFragment pChatView){
        DaggerSingleton.getInstance().daggerComponent().inject(this);

        mChatView = pChatView;

        DatabaseHelper.init(mContext);
    }

    public void initChatParams(XMPPServerConfig pServerConfig, String pUserName, String pPassword, String pChatterName) {
        mUserName = pUserName;
        mPassword = pPassword;
        mChatterName = pChatterName;

        mServerConfig = pServerConfig;

    }

    private void initXMPPService() {
        mServerConfig.setSASLAuthenticationEnabled(true);

        mXmppService.init(mServerConfig);

        mXmppService.connect(mUserName, mPassword, new ConnectionStateListener() {

            @Override
            public void loggingFailed() {
                mChatView.showToastMessage(getStringFromRes(R.string.logging_failed));
            }

            @Override
            public void connectionFailed() {
                mChatView.removeLoaderView();
                mChatView.showToastMessage(getStringFromRes(R.string.connection_failed));
            }

            @Override
            public void connectedAndLogged() {
                mChatView.removeLoaderView();
                mChatView.showToastMessage(getStringFromRes(R.string.connection_and_logged));
            }
        });
        mXmppService.setMessageReceiver(mChatterName, new XMPPOnMessageReceivedListener() {

            @Override
            public void messageReceived(final XMPPMessage message) {
                mMessageDB.saveMessageInDB(message);

                mChatView.addMessage(message);
                mChatView.scrollDownMessageListView(true);

            }
        });

        List<XMPPMessage> messageList = mMessageDB.getLastMessages(mUserName, mChatterName);

        mChatView.setMessageList(messageList);
        mChatView.scrollDownMessageListView(false);
    }

    public void viewCreated() {
        initXMPPService();
    }

    public void viewDestroyed(){
        mXmppService.disconnect();
    }

    public void viewStopped() {
        if (mXmppService != null) {
            mXmppService.setMessageReceiver(mChatterName, null);
        }
    }

    public void sendMessage(final String message) {
        if (message.length() == 0)
            return;

        mChatView.showMessageSendingLoader();;
        mChatView.hideSendButton();

        mXmppService.sendMessage(mChatterName, message, new MessageSendingListener() {

            @Override
            public void messageSent(XMPPMessage pMessage) {

                mMessageDB.saveMessageInDB(pMessage);

                mChatView.showToastMessage(getStringFromRes(R.string.message_sent));
                mChatView.hideMessageSendingLoader();
                mChatView.showSendButton();

                mChatView.emptyMessageEdit();
                mChatView.addMessage(pMessage);

                mChatView.scrollDownMessageListView(true);

            }

            @Override
            public void messageNotSent(XMPPMessage pMessage) {
                mChatView.showToastMessage(getStringFromRes(R.string.message_not_sent));

            }
        });
    }

    private String getStringFromRes(int pStringId){
        return mContext.getString(pStringId);
    }


}
