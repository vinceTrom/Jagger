package com.vtromeur.jagger.xmpp.iq;

import org.jivesoftware.smack.packet.IQ;

/**
 * The Token IQ is used to store the user push token to the server
 * Created by Vincent Tromeur on 04/12/15.
 */
public class TokenIQ extends IQ {

    private String _token;
    private String _platform;

    public TokenIQ(String platform, String token) {
        _platform = platform;
        _token = token;
        setType(Type.SET);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns='urn:xmpp:tokeniq'><token>" + _token + "</token><platform>" + _platform + "</platform></query>";
    }

}
