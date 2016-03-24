package com.vtromeur.jagger.xmpp;

import java.io.Serializable;

/**
 *
 * Created by Vincent Tromeur on 10/12/15.
 */
public class XMPPServerConfig implements Serializable{

    private String HOST;
    private String PORT;
    private String USERNAME_SUFFIX;

    private boolean mMustAppendUsernameSuffix = true;
    private boolean mSASLAuthenticationEnabled = false;

    /**
     * Create a new Server Configuration
     * @param pHost the Host adress of the XMPP server
     * @param pPort the port number used by the XMPP server
     */
    public XMPPServerConfig(String pHost, String pPort) {
        HOST = pHost;
        PORT = pPort;
        USERNAME_SUFFIX = "no_username_suffix_defined";
    }

    /**
     * Create a new Server Configuration
     * @param pHost the Host adress of the XMPP server
     * @param pPort the port number used by the XMPP server
     * @param pUserNameSuffix the username suffix if needed, is usually the same value as the host
     */
    public XMPPServerConfig(String pHost, String pPort, String pUserNameSuffix) {
        HOST = pHost;
        PORT = pPort;
        USERNAME_SUFFIX = pUserNameSuffix;
    }


    public boolean isSASLAuthenticationEnabled() {
        return mSASLAuthenticationEnabled;
    }

    public void setSASLAuthenticationEnabled(boolean pSASLAuthenticationEnabled) {
        mSASLAuthenticationEnabled = pSASLAuthenticationEnabled;
    }

    public boolean isMustAppendUsernameSuffix() {
        return mMustAppendUsernameSuffix;
    }

    public void setMustAppendUsernameSuffix(boolean pMustAppendUsernameSuffix) {
        mMustAppendUsernameSuffix = pMustAppendUsernameSuffix;
    }

    public String getHost() {
        return HOST;
    }

    public String getPort() {
        return PORT;
    }

    public String getUsernameSuffix() {
        return USERNAME_SUFFIX;
    }
}
