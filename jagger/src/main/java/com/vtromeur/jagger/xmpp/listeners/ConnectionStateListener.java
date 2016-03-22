package com.vtromeur.jagger.xmpp.listeners;

/**
 * Created by Vincent Tromeur on 04/12/15.
 */
public interface ConnectionStateListener {

    int NOT_CONNECTED = 0;
    int CONNECTED = 1;
    int AUTHENTICATED = 2;


    void connectionFailed();

    void loggingFailed();

    void connectedAndLogged();
}
