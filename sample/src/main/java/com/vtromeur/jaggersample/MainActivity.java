package com.vtromeur.jaggersample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vtromeur.jagger.ChatFragment;
import com.vtromeur.jagger.UICustomization;
import com.vtromeur.jagger.di.DaggerSingleton;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;

public class MainActivity extends AppCompatActivity {

    private static final String XMPP_SERVER_HOST = "securejabber.me";
    private static final String XMPP_SERVER_PORT = "5222";
    private static final String XMPP_SERVER_NAME_SUFFIX = "securejabber.me";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchCredentialsFragment();

        DaggerSingleton.initInstance(getBaseContext());
    }

    private void launchCredentialsFragment(){
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserCredentialsFragment()).commit();

    }

    public void launchChatFragment(String pUserId, String pUserPassword, String pChatterId){
        XMPPServerConfig serverConfig = new XMPPServerConfig(XMPP_SERVER_HOST, XMPP_SERVER_PORT, XMPP_SERVER_NAME_SUFFIX);
        ChatFragment chatFragment = ChatFragment.getInstance(serverConfig, pUserId, pUserPassword, pChatterId);

        UICustomization uiCustomization = new UICustomization();
        uiCustomization.mUserMessageTextColor = Color.WHITE;
        uiCustomization.mUserMessageBackgroundColorResource = R.color.facebook_blue;
        uiCustomization.mChatterMessageTextColor = Color.BLACK;
        uiCustomization.mChatterMessageBackgroundColorResource = R.color.grey;
        chatFragment.setUICustomization(uiCustomization);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();
    }

}
