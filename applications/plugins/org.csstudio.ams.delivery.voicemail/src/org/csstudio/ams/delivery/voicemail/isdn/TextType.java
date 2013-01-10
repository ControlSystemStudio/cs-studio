
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.voicemail.isdn;

/**
 * @author mmoeller
 * @version 1.0
 * @since 08.02.2012
 */
public enum TextType {
    
    /**  */
    INVALID(0),
    
    /** alarm without confirmation */
    ALARM_WOCONFIRM(1),

    /** alarm with confirmation */
    ALARM_WCONFIRM(2),

    /** alarm confirmation ok */
    ALARMCONFIRM_OK(3),

    /** alarm confirmation rejected */
    ALARMCONFIRM_NOK(4),

    /** status change ok */
    STATUSCHANGE_OK(5),

    /** status change rejected */
    STATUSCHANGE_NOK(6);

    private int typeNumber;
    
    private TextType(int n) {
        typeNumber = n;
    }
    
    public int getTypeNumber() {
        return typeNumber;
    }
    
    public static TextType getTextTypeByNumber(int n) {
        TextType result = TextType.INVALID;
        for (TextType o : TextType.values()) {
            if (o.getTypeNumber() == n) {
                result = o;
            }
        }
        return result;
    }
    
    public static TextType getTextTypeByNumber(String n) {
        int type = 0;
        try {
            type = Integer.parseInt(n);
        } catch (NumberFormatException nfe) {
            type = 0;
        }
        return TextType.getTextTypeByNumber(type);
    }
}
