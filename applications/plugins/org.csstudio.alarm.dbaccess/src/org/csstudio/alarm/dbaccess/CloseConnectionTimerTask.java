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
package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimerTask;

import org.csstudio.platform.logging.CentralLogger;

/**
 * The TimerTask checks the period of the last dbAccess
 * and closes the connection if the period is longer than a
 * threshold.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 15.05.2008
 */
public class CloseConnectionTimerTask extends TimerTask {

	private long _lastDBAcccessInMillisec;
	
	private Connection _dbConnection;
	
	private long _closeThresholdInMillisec = 30 * 60 * 1000;
	
	public CloseConnectionTimerTask(Connection con) {
		_dbConnection = con;
	}
	
	@Override
	public void run() {
		long lastConnectionPeriod = System.currentTimeMillis() - _lastDBAcccessInMillisec;
		if (lastConnectionPeriod > _closeThresholdInMillisec) {
			try {
				_dbConnection.close();
				this.cancel();
			} catch (SQLException e) {
				CentralLogger.getInstance().error(this, "Close SQL Connection error " + e.getMessage());
			}
		}
	}

	public void set_lastDBAcccessInMillisec(long acccessInMillisec) {
		_lastDBAcccessInMillisec = acccessInMillisec;
	}

}
