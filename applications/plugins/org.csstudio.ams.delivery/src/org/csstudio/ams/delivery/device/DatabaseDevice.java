
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.delivery.device;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.csstudio.ams.delivery.BaseAlarmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class DatabaseDevice implements IDeliveryDevice {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseDevice.class);
    
    protected String deviceId;
    
    protected Driver dbDriver;
    
    protected String dbUrl;
    
    protected String dbUser;
    
    protected String dbPassword;
    
    public DatabaseDevice(Driver driver, String id, String url, String user, String password) {
        dbDriver = driver;
        deviceId = id;
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
        init();
    }

    private void init() {
        try {
            DriverManager.registerDriver(dbDriver);
        } catch (SQLException sqle) {
            LOG.error("Cannot initialize DatabaseGateway.", sqle, deviceId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendMessage(BaseAlarmMessage message) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BaseAlarmMessage receiveMessage() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stopDevice() {
        if (dbDriver != null) {
            try {
                DriverManager.deregisterDriver(dbDriver);
            } catch (SQLException e) {
                // Ignore me
            }
        }
    }
}
