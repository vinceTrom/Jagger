package com.vtromeur.jagger.xmpp.iq;

import org.jivesoftware.smack.packet.IQ;

/**
 * The FirstName IQ is used to store the user name to the server
 * Created by Vincent Tromeur on 04/12/15.
 */
public class FirstNameIQ extends IQ {

    private String _firstName;

    public FirstNameIQ(String firstName) {
        _firstName = firstName;
        setType(Type.SET);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns=\"urn:xmpp:firstnameiq\"><firstname>" + _firstName + "</firstname></query>";
    }

}
