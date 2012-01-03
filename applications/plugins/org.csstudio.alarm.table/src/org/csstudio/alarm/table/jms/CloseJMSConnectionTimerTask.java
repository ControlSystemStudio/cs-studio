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
package org.csstudio.alarm.table.jms;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The TimerTask checks the period of the last JMSAccess
 * and closes the connection if the period is longer than a
 * threshold.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 15.05.2008
 */
public class CloseJMSConnectionTimerTask extends TimerTask {
    
    private static final Logger LOG = LoggerFactory.getLogger(CloseJMSConnectionTimerTask.class);

	private long _lastDBAcccessInMillisec;
	
	private final SendMapMessage _sender;
	
	private final long _closeThresholdInMillisec = 5 * 1000;
	
	public CloseJMSConnectionTimerTask(SendMapMessage sender) {
		_sender = sender;
	}
	
	@Override
	public synchronized void run() {
		long lastConnectionPeriod = System.currentTimeMillis() - _lastDBAcccessInMillisec;
		if (lastConnectionPeriod > _closeThresholdInMillisec) {
				try {
					LOG.debug("TimerTask stops JMS Connection");
					_sender.stopSender();
					this.cancel();
				} catch (Exception e) {
					LOG.error("Close JMS Connection error: ", e);
				}
		}
	}

	public void set_lastDBAcccessInMillisec(long acccessInMillisec) {
		LOG.debug("Reset time of JMS Close Connection Timer Task");
		_lastDBAcccessInMillisec = acccessInMillisec;
	}

}
