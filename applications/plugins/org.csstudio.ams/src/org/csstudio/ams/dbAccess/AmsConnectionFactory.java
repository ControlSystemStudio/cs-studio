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
import org.csstudio.ams.Activator;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.internal.SampleService;
import org.eclipse.jface.preference.IPreferenceStore;

public class AmsConnectionFactory 
{
	public static Connection getConfigurationDB() throws ClassNotFoundException, SQLException 
	{
		//DriverManager.setLogWriter(new java.io.PrintWriter(System.out));		
		DriverManager.registerDriver(new OracleDriver());
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String dbCon = store.getString(SampleService.P_CONFIG_DATABASE_CONNECTION);
		String user = store.getString(SampleService.P_CONFIG_DATABASE_USER); 
		String pwd = store.getString(SampleService.P_CONFIG_DATABASE_PASSWORD);

		Log.log(Log.INFO, "try getConfigurationDB to " + dbCon);
		Log.log(Log.INFO, "try getConfigurationDB user " + user);
		
		return DriverManager.getConnection(dbCon, user, pwd);
	}

	public static Connection getApplicationDB() throws ClassNotFoundException, SQLException 
	{
		//DriverManager.setLogWriter(new java.io.PrintWriter(System.out));
		DriverManager.registerDriver(new ClientDriver());
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String user = store.getString(SampleService.P_APP_DATABASE_USER); 
		if (Utils.isEmpty(user))
			user = null;
		
		String pwd = store.getString(SampleService.P_APP_DATABASE_PASSWORD); 
		if (Utils.isEmpty(pwd))
			pwd = null;
		
		return DriverManager.getConnection(store.getString(SampleService.P_APP_DATABASE_CONNECTION), 
				user, 
				pwd);
	}
	
	public static void closeConnection(Connection conDb)
	{
		try
		{
			if (conDb != null)
				conDb.close();
		}
		catch(Exception ex)
		{
			Log.log(Log.WARN, ex);
		}
	}
}
