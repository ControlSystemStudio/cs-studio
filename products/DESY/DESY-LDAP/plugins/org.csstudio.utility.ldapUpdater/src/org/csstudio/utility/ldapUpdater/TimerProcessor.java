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

package org.csstudio.utility.ldapUpdater;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

public class TimerProcessor {
    
    static class ProcessOnTime extends TimerTask {
        
        private static final Logger LOGGER = CentralLogger.getInstance().getLogger(ProcessOnTime.class);
        
        @Override
        public void run() {
            
            LdapUpdater ldapUpdater = LdapUpdater.getInstance();
            try {
                long time = 0L;
                boolean timeOut = false;
                
                while (ldapUpdater.isBusy()) {
                    if (time < LDAP_TIMEOUT) {
                        LOGGER.error("LDAP Update Time out. Service still busy after " + LDAP_TIMEOUT / 1000 + "s.");
                        timeOut = true;
                        break;
                    }
                    time += LDAP_RECHECK;
                    Thread.sleep(LDAP_RECHECK);
                }
                
                if (!timeOut) {
                    ldapUpdater.updateLdapFromIOCFiles();
                }
                
            } catch (InterruptedException ie) {
                // TODO (bknerr) : check appropriate thread handling (see Goetz book)
                ie.printStackTrace();
            } catch (Exception e) {
                LOGGER.info  ("LdapUpdater is busy" );
                LOGGER.error ("LdapUpdater is busy" );
            }
        }
    }
    private static long delay;
    private static long interval;
    private static long LDAP_RECHECK = 10000; // every 10 seconds
    
    private static long LDAP_TIMEOUT = 300000; // until 300 seconds are over
    
    
    public static long getDelay() {
        return delay;
    }
    
    public static long getInterval() {
        return interval;
    }
    
    public static void setDelay(long delay) {
        TimerProcessor.delay = delay;
    }
    
    public static void setInterval(long interval) {
        TimerProcessor.interval = interval;
    }
    
    TimerProcessor( long delay, long interval) {
        TimerProcessor.delay = delay;
        TimerProcessor.interval = interval;
        
        execute();
    }
    
    public void execute() {
        Timer timer = new Timer();
        TimerTask processOnTime = new ProcessOnTime();
        timer.scheduleAtFixedRate(processOnTime, getDelay(), getInterval());
    }
}