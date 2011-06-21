
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.websuite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;

/**
 * @author Markus Moeller
 *
 */
public class DatabaseHandler {
    
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    
    public DatabaseHandler(String url, String user, String password) throws SQLException {
        
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = password;
        
        DriverManager.registerDriver(new OracleDriver());
    }

    public String getHowToEntryText(String strId) throws SQLException {
        
        long id;
        
        try {
            id = Long.parseLong(strId);
        } catch(NumberFormatException nfe) {
            id = 0;
        }
        
        return this.getHowToEntryText(id);
    }
    
    public String getHowToEntryText(long id) throws SQLException {
        
        Connection c = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        String result = null;
        
        try {
            c = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            query = c.prepareStatement("SELECT desclong FROM howto WHERE howtoid = ?");
            
            query.setLong(1, id);
            rs = query.executeQuery();
            
            if(rs.next()) {
                result = rs.getString("desclong");
            }
        } catch(SQLException sqle) {
            throw sqle;
        } finally {
            if(rs!=null){try{rs.close();}catch(Exception e){/* Can be ignored */}rs=null;}
            if(query!=null){try{query.close();}catch(Exception e){/* Can be ignored */}query=null;}
            if(c!=null){try{c.close();}catch(Exception e){/* Can be ignored */}c=null;}
        }
        
        return result;
    }
}
