package gov.bnl.shiftClient;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

class ClientResponseParser extends HTMLEditorKit.ParserCallback {

    private String message = "";
    boolean messageFlag = false;
    boolean causeFlag = false;

    public ClientResponseParser() {
        super();
    }

    public void handleText(final char[] data, final int pos) {
        String strData = new String(data);
        if(messageFlag){
            message += strData;
            messageFlag = false;
        }else if(causeFlag) {
            message += " - " + strData;
            causeFlag = false;
        }else {
            if(strData.equalsIgnoreCase("description"))
                messageFlag = true;
            else if(strData.equalsIgnoreCase("caused by:"))
                causeFlag = true;
        }
    }

    public void handleStartTag(final HTML.Tag t, MutableAttributeSet a, final int p) {
    }

    public String getMessage() {
        return this.message;
    }
}
