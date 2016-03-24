package com.vtromeur.jaggersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vtromeur.jagger.ChatFragment;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;

public class MainActivity extends AppCompatActivity {

    private static final String XMPP_SERVER_HOST = "securejabber.me";
    private static final String XMPP_SERVER_PORT = "5222";
    private static final String XMPP_SERVER_NAME_SUFFIX = "securejabber.me";

    private static final String USER_XMPP_ID = "YOUR ID ON THE JABBER SERVER";
    private static final String USER_XMPP_PASSWORD = "YOUR PASSWORD";
    private static final String CHATTER_XMPP_ID = "THE ID OF THE USER YOU WANT TO CHAT WITH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XMPPServerConfig serverConfig = new XMPPServerConfig(XMPP_SERVER_HOST, XMPP_SERVER_PORT, XMPP_SERVER_NAME_SUFFIX);
        ChatFragment chatFragment = ChatFragment.getInstance(serverConfig, USER_XMPP_ID, USER_XMPP_PASSWORD, CHATTER_XMPP_ID);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment).commit();
    }

}
