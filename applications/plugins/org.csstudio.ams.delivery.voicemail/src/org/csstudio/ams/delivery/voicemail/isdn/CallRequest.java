
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

/**
 * @author Markus Moeller
 *
 */
public class CallRequest {
    
    private String msgText;
    private String telephoneNumber;
    private int chainIdAndPos;
    private int textType;
    private long waitUntil;

    public CallRequest(String text, String phoneNumber,
                       int chainId, int type, long waitTime) {
        
        this.msgText = text;
        this.telephoneNumber = phoneNumber;
        this.chainIdAndPos = chainId;
        this.textType = type;
        this.waitUntil = waitTime;
    }
    
    public CallRequest(String text, String phoneNumber,
                       String chainId, String type, String waitTime) {
        
        this.msgText = text;
        this.telephoneNumber = phoneNumber;
        
        try {
            this.chainIdAndPos = Integer.parseInt(chainId);
        } catch(NumberFormatException nfe) {
            this.chainIdAndPos = 0;
        }
        
        try {
            this.textType = Integer.parseInt(type);
        } catch(NumberFormatException nfe) {
            this.textType = 0;
        }

        try {
            this.waitUntil = Long.parseLong(waitTime);
        } catch(NumberFormatException nfe) {
            this.waitUntil = 0L;
        }
    }
}
