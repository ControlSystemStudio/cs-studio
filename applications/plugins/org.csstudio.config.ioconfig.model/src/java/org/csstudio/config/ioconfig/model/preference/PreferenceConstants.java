/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: PreferenceConstants.java,v 1.3 2009/08/31 12:11:45 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.preference;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 24.04.2009
 */
public final class PreferenceConstants {
    
    public static final String DDB_FACILITIES = "ddbFacilities";
    public static final String DDB_LOGBOOK = "ddbLogbook";
    public static final String DDB_LOGBOOK_MEANING = "ddbLogbookMeaning";
    public static final String DDB_USER_NAME = "ddbUserName";
    public static final String DDB_PASSWORD = "ddbPassword";
    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernateConnectionDriverClass";
    public static final String HIBERNATE_CONNECTION_URL = "hibernateConnectionUrl";
    public static final String DIALECT = "dialect";
    public static final String SHOW_SQL = "showSql";
    public static final String DDB_TIMEOUT= "ddb_connection_timeout";

    /**
     * Constructor.
     */
    private PreferenceConstants() {
        // Constructor.
    }
}
