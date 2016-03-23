package com.vtromeur.jagger.xmpp.iq;

import org.jivesoftware.smack.packet.IQ;

/**
 * The Token IQ is used to store the user push token to the server
 * Created by Vincent Tromeur on 04/12/15.
 */
public class TokenIQ extends IQ {

    private String mToken;
    private String mPlatform;

    public TokenIQ(String platform, String token) {
        mPlatform = platform;
        mToken = token;
        setType(Type.SET);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns='urn:xmpp:tokeniq'><token>" + mToken + "</token><platform>" + mPlatform + "</platform></query>";
    }

}
