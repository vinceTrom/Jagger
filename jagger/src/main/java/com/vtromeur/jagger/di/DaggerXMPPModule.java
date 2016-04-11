package com.vtromeur.jagger.di;

import com.vtromeur.jagger.xmpp.XMPPService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vince on 08/04/16.
 */
@Module
public class DaggerXMPPModule {

    @Singleton
    @Provides
    public XMPPService provideXMPPPService(){
        return new XMPPService();
    }
}
