package com.vtromeur.jagger.xmpp.iq;

/**
 * The Token IQ is used to store the user push token to the server
 * Created by Vincent Tromeur on 04/12/15.
 */
public class TokenIQ {

    private String mToken;
    private String mPlatform;

    public TokenIQ(String platform, String token) {
        mPlatform = platform;
        mToken = token;
        //setType(Type.SET);
    }

    public String getChildElementXML() {
        return "<query xmlns='urn:xmpp:tokeniq'><token>" + mToken + "</token><platform>" + mPlatform + "</platform></query>";
    }

}
