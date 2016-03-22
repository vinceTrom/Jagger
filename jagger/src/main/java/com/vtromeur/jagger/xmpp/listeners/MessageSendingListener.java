package com.vtromeur.jagger.xmpp.listeners;

import com.vtromeur.jagger.xmpp.XMPPMessage;

/**
 * Created by Vincent Tromeur on 04/12/15.
 */
public interface MessageSendingListener {

    int MESSAGE_SENT = 0;
    int MESSAGE_NOT_SENT = 1;

    void messageSent(XMPPMessage pMessage);

    void messageNotSent(XMPPMessage pMessage);

}
