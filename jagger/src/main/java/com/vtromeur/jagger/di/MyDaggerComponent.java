package com.vtromeur.jagger.di;

import com.vtromeur.jagger.ChatPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vince on 08/04/16.
 */
@Singleton
@Component(modules = DaggerXMPPModule.class)
public interface MyDaggerComponent {

    void inject(ChatPresenter pChatPresenter);
}
