package com.vtromeur.jagger.xmpp.listeners;

/**
 * Created by Vincent Tromeur on 04/12/15.
 */
public interface XMPPAccountCreationListener {

    int ACCOUNT_CREATED = 0;

    int ACCOUNT_NOT_CREATED = 1;

    void accountCreated();

    void accountCreationFailed();
}
