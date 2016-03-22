package com.vtromeur.jagger.xmpp.listeners;

import com.vtromeur.jagger.xmpp.XMPPMessage;

/**
 * Created by Vincent Tromeur on 08/12/15.
 */
public interface XMPPOnMessageReceivedListener {

    void messageReceived(XMPPMessage message);
}
