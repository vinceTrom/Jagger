package com.vtromeur.jagger.xmpp.iq;

/**
 * The FirstName IQ is used to store the user name to the server
 * Created by Vincent Tromeur on 04/12/15.
 */
public class FirstNameIQ  {

    private String mFirstName;

    public FirstNameIQ(String firstName) {
        mFirstName = firstName;
        //setType(Type.SET);
    }

    public String getChildElementXML() {
        return "<query xmlns=\"urn:xmpp:firstnameiq\"><firstname>" + mFirstName + "</firstname></query>";
    }


}
