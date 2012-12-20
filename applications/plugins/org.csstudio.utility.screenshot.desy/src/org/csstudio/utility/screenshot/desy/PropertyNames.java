
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

package org.csstudio.utility.screenshot.desy;

/**
 * @author mmoeller
 * @version 1.0
 * @since 08.08.2012
 */
public class PropertyNames {
    
    public static final String PROPERTY_IDENTIFYER = "IDENTIFYER";
    public static final String PROPERTY_ACCOUNTNAME = "ACCOUNTNAME";
    public static final String PROPERTY_LOGGROUP = "LOGGROUP";
    public static final String PROPERTY_ENTRYDATE = "ENTRYDATE";
    public static final String PROPERTY_EVENTFROM = "EVENTFROM";
    public static final String PROPERTY_EVENTUNTIL = "EVENTUNTIL";
    public static final String PROPERTY_TITLE = "DESCSHORT";
    public static final String PROPERTY_TEXT = "DESCLONG";
    public static final String PROPERTY_MULTIMEDIAIDENTIFYER = "MULTIMEDIAIDENTIFYER";
    public static final String PROPERTY_ERRORIDENTIFYER = "ERRORIDENTIFYER";
    public static final String PROPERTY_PREVIOUSOPERATORLOG = "PREVIOUSOPERATORLOG";
    public static final String PROPERTY_STOCKTRANSACTION = "STOCKTRANSACTION";
    public static final String PROPERTY_HTMLLINK = "HTMLLINK";
    public static final String PROPERTY_PROJECT = "PROJECT";
    public static final String PROPERTY_DEVICE = "DEVICE";
    public static final String PROPERTY_LOCATION = "LOCATION";
    public static final String PROPERTY_KEYWORDS = "KEYWORDS";
    public static final String PROPERTY_SENDEMAILTO = "SENDEMAILTO";
    public static final String PROPERTY_MAINTENANCE = "MAINTENANCE";
    public static final String PROPERTY_LOGSEVERITY = "LOGSEVERITY";

    public static final int PROPERTYCOUNT = 20;

    private static String[] keyList;
    
    static {
        keyList = new String[PropertyNames.PROPERTYCOUNT];
        keyList[0] = PropertyNames.PROPERTY_IDENTIFYER;
        keyList[1] = PropertyNames.PROPERTY_ACCOUNTNAME;
        keyList[2] = PropertyNames.PROPERTY_LOGGROUP;
        keyList[3] = PropertyNames.PROPERTY_ENTRYDATE;
        keyList[4] = PropertyNames.PROPERTY_EVENTFROM;
        keyList[5] = PropertyNames.PROPERTY_EVENTUNTIL;
        keyList[6] = PropertyNames.PROPERTY_TITLE;
        keyList[7] = PropertyNames.PROPERTY_TEXT;
        keyList[8] = PropertyNames.PROPERTY_MULTIMEDIAIDENTIFYER;
        keyList[9] = PropertyNames.PROPERTY_ERRORIDENTIFYER;
        keyList[10] = PropertyNames.PROPERTY_PREVIOUSOPERATORLOG;
        keyList[11] = PropertyNames.PROPERTY_STOCKTRANSACTION;
        keyList[12] = PropertyNames.PROPERTY_HTMLLINK;
        keyList[13] = PropertyNames.PROPERTY_PROJECT;
        keyList[14] = PropertyNames.PROPERTY_DEVICE;
        keyList[15] = PropertyNames.PROPERTY_LOCATION;
        keyList[16] = PropertyNames.PROPERTY_KEYWORDS;
        keyList[17] = PropertyNames.PROPERTY_SENDEMAILTO;
        keyList[18] = PropertyNames.PROPERTY_MAINTENANCE;
        keyList[19] = PropertyNames.PROPERTY_LOGSEVERITY;
    }

    public static String[] getKeyList() {
        return keyList;
    }
    
    private PropertyNames() {}
}
