
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams.delivery.voicemail.isdn;

import org.csstudio.ams.delivery.voicemail.isdn.TextType;

/**
 * @author Markus Moeller
 *
 */
@SuppressWarnings("hiding")
public class CallInfo {
    
    private String chainIdAndPos;
    private String telephoneNumber;
    private String confirmationCode;
    private String statusCode;
    private TextType textType;
    private boolean success;
    
    public CallInfo() {
        chainIdAndPos = "";
        telephoneNumber = "";
        confirmationCode = "";
        statusCode = "";
        textType = TextType.INVALID;
        success = false;
    }

    public CallInfo(String telephoneNumber, TextType textType) {
        this.telephoneNumber = telephoneNumber;
        this.textType = textType;
        chainIdAndPos = "";
        confirmationCode = "";
        statusCode = "";
        success = false;
    }

    public CallInfo(String telephoneNumber, TextType textType, String chainIdAndPos) {
        this.telephoneNumber = telephoneNumber;
        this.textType = textType;
        this.chainIdAndPos = chainIdAndPos;
        confirmationCode = "";
        statusCode = "";
        success = false;
    }

    public String getChainIdAndPos() {
        return chainIdAndPos;
    }

    public void setChainIdAndPos(String chainIdAndPos) {
        this.chainIdAndPos = chainIdAndPos;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public TextType getTextType() {
        return textType;
    }

    public int getTextTypeAsNumber() {
        return textType.ordinal();
    }

    public void setTextType(TextType textType) {
        this.textType = textType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
