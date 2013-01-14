
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.ams.delivery.message.BaseAlarmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class DatabaseDevice implements IDeliveryDevice<BaseAlarmMessage>,
                                       IReadableDevice<BaseAlarmMessage> {
    
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

    @Override
    public boolean deleteMessage(BaseAlarmMessage msg) {
        
        Connection con = null;
        PreparedStatement query = null;
        boolean success = false;
        
        String sql = "DELETE FROM Gateway WHERE cFromGateway = ? AND cReceiverAddress = ? AND cMessageText = ?";
        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            query = con.prepareStatement(sql);
            query.setString(1, msg.getDeviceId());
            query.setString(2, msg.getReceiverAddress());
            query.setString(3, msg.getMessageText());
            query.execute();
            success = (query.getUpdateCount() == 1);
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (query != null) {
                try{query.close();}catch(SQLException e) {
                    // Ignore me
                }
            }
            if (con != null) {
                try{con.close();}catch(SQLException e) {
                    // Ignore me
                }
            }
        }
        
        return success;
    }

    private boolean sendMessage(Connection con, BaseAlarmMessage msg) {
        
        boolean success = false;
        String sql = "INSERT INTO Gateway (cFromGateway,cReceiverAddress,cMessageText) VALUES (?,?,?)";
        PreparedStatement query = null;
        
        try {
            query = con.prepareStatement(sql);
            query.setString(1, msg.getDeviceId());
            query.setString(2, msg.getReceiverAddress());
            query.setString(3, msg.getMessageText());
            query.execute();
            success = true;
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (query != null) {
                try { query.close(); } catch (SQLException e) {
                    // Ignore me
                }
            }
        }
        
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendMessage(BaseAlarmMessage message) {
        Connection con = null;
        boolean success = false;
        
        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            success = sendMessage(con, message);
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (con != null) {
                try{con.close();}catch(SQLException e) {
                    // Ignore me
                }
            }
        }
        
        return success;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int sendMessages(Collection<BaseAlarmMessage> msgList) {
        
        Connection con = null;
        int cnt = 0;

        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            for (BaseAlarmMessage msg : msgList) {
                if (sendMessage(con, msg)) cnt++;
            }
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (con != null) {
                try{con.close();}catch(SQLException e) {
                    // Ignore me
                }
            }
        }
            
        return cnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseAlarmMessage readMessage() {
        return null;
    }
    
    @Override
    public int readMessages(Collection<BaseAlarmMessage> msgList) {
        
        Connection con = null;

        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            ArrayList<BaseAlarmMessage> msg = readMessages(con);
            msgList.addAll(msg);
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (con != null) {
                try{con.close();}catch(SQLException e) {
                    // Ignore me
                }
            }
        }
        
        return msgList.size();
    }

    public ArrayList<BaseAlarmMessage> readMessages(Connection con) {
        
        String sql = "SELECT * FROM Gateway WHERE cMessageText LIKE '%MODEMTEST%'";
        
        ArrayList<BaseAlarmMessage> msg = new ArrayList<BaseAlarmMessage>();
        
        PreparedStatement query = null;
        ResultSet rs = null;
        
        try {
            query = con.prepareStatement(sql);
            rs = query.executeQuery();
            while (rs.next()) {
                String addr = rs.getString("cReceiverAddress");
                String text = rs.getString("cMessageText");
                String gw = rs.getString("cFromGateway");
                                
                BaseAlarmMessage o = new BaseAlarmMessage(System.currentTimeMillis(),
                                                          BaseAlarmMessage.Priority.NORMAL,
                                                          addr,
                                                          text,
                                                          BaseAlarmMessage.State.NEW,
                                                          BaseAlarmMessage.Type.IN,
                                                          gw);
                msg.add(o);
            }
        } catch (SQLException sqle) {
            LOG.error("[*** SQLException ***]: " + sqle.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) {
                    // Ignore me
                }
            }
            if (query != null) {
                try { query.close(); } catch (SQLException e) {
                    // Ignore me
                }
            }
        }

        return msg;
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
