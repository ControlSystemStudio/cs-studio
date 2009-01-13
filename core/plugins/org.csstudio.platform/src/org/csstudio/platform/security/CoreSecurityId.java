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
 * $Id$
 */
package org.csstudio.platform.security;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.01.2009
 */
public enum CoreSecurityId implements ISecurityId {
    
    IOC_ACCESSES("iocAccesses", "Permission for IOC tools", "Deny"),
    AMS_CONFIG("amsconfigurator","Permission to open and use the AMS Configurator.","Allow"),
    EXAMPLE("example", "Used in the example configuration file. Not used by any actions.","n/a"),
    REMOTE_MANAGEMENT("remoteManagement", "Permission to run remote management actions in the directory viewer via the context menu.",   "Deny"),
    RESTART("restart", "Permission to execute the \"Restart\" action in the CSS.", "Allow"),
    AUTHORIZE_ID("AuthorizeId", "Permission to configure the Authorization Id", "Deny"),
    OPERATING("operating",   "Permission for operating/ alarm acknowledge", "Deny"),
    TESTING("testing", "Permission for testing tools.", "Deny"),
    ALARM_ADMINISTARTION("alarmAdministration", "Administration of alarm configuration and database", "Deny");

    
    private String _id;
    private String _desc;
    private String _defAccess;

    private CoreSecurityId(String id, String desc) {
        _id = id;
        _desc = desc;
    }

    private CoreSecurityId(String id, String desc, String defAccess) {
        _id = id;
        _desc = desc;
        _defAccess = defAccess;
    }

    
    public String getDesciption() {
        return _desc;
    }

    public String getId() {
        // TODO Auto-generated method stub
        return _id;
    }

    public String getDefaultAccesses() {
        return _defAccess;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getId();
    }
    
    

}
