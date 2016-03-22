package com.vtromeur.jagger.xmpp;

/**
 * Created by Vincent Tromeur on 10/12/15.
 */
public class XMPPServerConfig {

    private String HOST;
    private String PORT;
    private String USERNAME_SUFFIX;

    private boolean mMustAppendUsernameSuffix = true;
    private boolean mSASLAuthenticationEnabled = false;


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
