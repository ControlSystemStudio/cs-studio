
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
 */

package org.csstudio.ams.connector.sms.service;

/**
 * The class just contains some default values.
 * 
 * @author Matthias Clausen, Markus Moeller
 *
 */

public final class Property
{    
    /** Date format for the identifyer */
    public final static String LOGBOOK_DATE_FORMAT = "yyMMdd-HH:mm:ss";
    
    /** Normal date format */
    public final static String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** Default entry for the UPDDATE_DATE column */
    public final static String DEFAULT_UPDATE_DATE = "2000-01-01 00:00:01.0";

    /** AMS date format */
    public final static String AMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /** Default entry for the UPDDATE_DATE column */
    public final static String DEFAULT_UPDATE_DATE_INSERT = "2000-01-01 00:00:01";
 
    /** Oracle Error number */
    public final static int ORA_IO_EXCEPTION = 17002;
    
    /** Oracle Error number */
    public final static int ORA_CLOSED_CONNECTION = 17008;
    
    private Property() {
        // Avoid instantiation
    }
}
