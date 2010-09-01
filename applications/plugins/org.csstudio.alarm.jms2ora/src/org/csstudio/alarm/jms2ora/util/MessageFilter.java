
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

package org.csstudio.alarm.jms2ora.util;

import org.apache.log4j.Logger;
import org.csstudio.alarm.jms2ora.Jms2OraPlugin;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Markus Moeller
 *
 */
public class MessageFilter {
    
	/** The instance of this object. */
    private static MessageFilter instance = null;
    
    /** Object that contains the messages for comparing */
    private MessageFilterContainer messages = null;
    
    /** Thread that checks the hash table containing the stored messages */
    private WatchDog watchdog = null;
    
    private long timePeriod;
    
    private long watchdogWaitTime;
    
    private MessageFilter() {
        
    	IPreferencesService prefs = Platform.getPreferencesService();

        timePeriod = prefs.getLong(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.WATCHDOG_PERIOD, 120000, null);
        watchdogWaitTime = prefs.getLong(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.WATCHDOG_WAIT, 60000, null); 
        
        int sendBound = prefs.getInt(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.FILTER_SEND_BOUND, 100, null);
        int maxSentMessages = prefs.getInt(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.FILTER_MAX_SENT_MESSAGES, 6, null);
        messages = new MessageFilterContainer(sendBound, maxSentMessages);
        
        watchdog = new WatchDog();
        watchdog.start();
    }

    public static synchronized MessageFilter getInstance() {
        
    	if(instance == null) {
            instance = new MessageFilter();
        }

        return instance;
    }
    
    public synchronized boolean shouldBeBlocked(MessageContent mc) {
        boolean blockIt = false;
        
        blockIt = messages.addMessageContent(mc);
        
        return blockIt;
    }
    
    public synchronized void stopWorking() {
        watchdog.interrupt();
    }
    
    public synchronized MessageFilterContainer getMessageFilterContainer() {
    	return messages;
    }
    
    public synchronized long getTimePeriod() {
    	return this.timePeriod;
    }
    
    public synchronized long getWatchdogWaitTime() {
    	return this.watchdogWaitTime;
    }

    /**
     * 
     * TODO (mmoeller) : 
     * 
     * @author mmoeller
     * @version $Revision: 1.7 $
     * @since 25.08.2010
     */
    public class WatchDog extends Thread {
        
    	private Logger logger = null;

        public WatchDog() {
            logger = CentralLogger.getInstance().getLogger(this);
            logger.info("WatchDog initialized");
        }
        
        @Override
		public void run() {
            
        	int count;
            
            while(!isInterrupted()) {
                
            	synchronized(this) {
                    
            		try {
                        wait(getWatchdogWaitTime());
                    } catch(InterruptedException ie) {
                        logger.info("WatchDog interrupted");
                        interrupt();
                    }
                    
                    logger.debug("WatchDog is looking. Number of stored messages: "
                    		     + getMessageFilterContainer().size());
                }
                
                synchronized(getMessageFilterContainer()) {
                    count = getMessageFilterContainer().removeInvalidContent(getTimePeriod());
                    logger.debug("WatchDog has removed " + count + " message(s).");
                }
            }
            
            logger.info("WatchDog is leaving.");
        }
    }
}