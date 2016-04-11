package com.vtromeur.jagger.di;

import com.vtromeur.jagger.ChatFragment;
import com.vtromeur.jagger.xmpp.XMPPService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vince on 08/04/16.
 */
@Singleton
@Component(modules = DaggerXMPPModule.class)
public interface MyDaggerComponent {

    XMPPService xmppService();
    void inject(ChatFragment pChatFragment);
}
