/**
 * Copyright (C) 2010-2012 Brookhaven National Laboratory
 * Copyright (C) 2010-2012 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms.
 */
package gov.bnl.channelfinder.api;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

/**
 * @author shroffk
 * 
 *         Parses the HTML pay load containing the details about the error TODO
 *         improve the parsing
 */
class ClientResponseParser extends ParserCallback {

    private String message = "";
    boolean messageFlag = false;
    boolean causeFlag = false;

    public ClientResponseParser() {
        super();
    }

    public void handleText(char[] data, int pos) {
        String strData = new String(data);
        if (messageFlag) {
            message += strData;
            messageFlag = false;
        } else if (causeFlag) {
            message += " - " + strData;
            causeFlag = false;
        } else {
            if (strData.equalsIgnoreCase("description"))
                messageFlag = true;
            else if (strData.equalsIgnoreCase("caused by:"))
                causeFlag = true;
        }
    }

    public void handleStartTag(Tag t, MutableAttributeSet a, int p) {
    }

    public String getMessage() {
        return this.message;
    }
}
