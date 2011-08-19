
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

package org.csstudio.ams.dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.driver.OracleDriver;
import org.apache.derby.jdbc.ClientDriver;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.hsqldb.jdbcDriver;
import com.mysql.jdbc.Driver;

public class AmsConnectionFactory
{
    public static Connection getConfigurationDB() throws SQLException {
        
        final IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();
        final String dbType = store.getString(AmsPreferenceKey.P_CONFIG_DATABASE_TYPE);
        final String dbCon = store.getString(AmsPreferenceKey.P_CONFIG_DATABASE_CONNECTION);
        final String user = store.getString(AmsPreferenceKey.P_CONFIG_DATABASE_USER);
        final String pwd = store.getString(AmsPreferenceKey.P_CONFIG_DATABASE_PASSWORD);

        if(dbType.toUpperCase().indexOf("ORACLE") > -1) {
            DriverManager.registerDriver(new OracleDriver());
        } else if(dbType.toUpperCase().indexOf("HSQL") > -1) {
            DriverManager.registerDriver(new jdbcDriver());
        } else if(dbType.toUpperCase().indexOf("MYSQL") > -1) {
            DriverManager.registerDriver(new Driver());
        }

        Log.log(Log.INFO, "try getConfigurationDB for DB " + dbType);
        Log.log(Log.INFO, "try getConfigurationDB to " + dbCon);
        Log.log(Log.INFO, "try getConfigurationDB user " + user);

        return DriverManager.getConnection(dbCon, user, pwd);
    }

    public static Connection getApplicationDB() throws SQLException {
        
        final IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();
        String dbType = store.getString(AmsPreferenceKey.P_APP_DATABASE_TYPE);
        if(StringUtil.isBlank(dbType)) {
            dbType = "DERBY";
        }
        
        if(dbType.toUpperCase().indexOf("DERBY") > -1) {
            DriverManager.registerDriver(new ClientDriver());
        } else if(dbType.toUpperCase().indexOf("MYSQL") > -1) {
            DriverManager.registerDriver(new Driver());
        }

        String user = store.getString(AmsPreferenceKey.P_APP_DATABASE_USER);
        if (StringUtil.isBlank(user)) {
            user = null;
        }

        String pwd = store.getString(AmsPreferenceKey.P_APP_DATABASE_PASSWORD);
        if (StringUtil.isBlank(pwd)) {
            pwd = null;
        }

        return DriverManager.getConnection(store.getString(AmsPreferenceKey.P_APP_DATABASE_CONNECTION),
                user,
                pwd);
    }

    public static void closeConnection(final Connection conDb) {
        try {
            if (conDb != null) {
                conDb.close();
            }
        } catch(final Exception ex) {
            Log.log(Log.WARN, ex);
        }
    }
}
