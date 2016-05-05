package com.vtromeur.jagger.di;

import android.content.Context;

import com.vtromeur.jagger.MessageDbHelper;
import com.vtromeur.jagger.xmpp.XMPPService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vince on 08/04/16.
 */
@Module
public class DaggerXMPPModule {

    private final Context mContext;

    public DaggerXMPPModule(Context pContext){
        mContext = pContext;
    }

    @Singleton
    @Provides
    public XMPPService provideXMPPPService(){
        return new XMPPService();
    }

    @Singleton
    @Provides
    public MessageDbHelper provideMessageDbHelper(){
        return new MessageDbHelper();
    }

    @Singleton
    @Provides
    public Context provideContext(){
        return mContext;
    }
}
