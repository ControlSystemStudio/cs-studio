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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

/**
 * The timer processor to schedule the LdapUpdater.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
public class TimerProcessor {

    private final Date _schedule;
    private final long _interval;

    private static long LDAP_RECHECK = 10000; // every 10 seconds
    private static long LDAP_TIMEOUT = 300000; // until 300 seconds are over

    /**
     * The task.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 13.04.2010
     */
    private static class ProcessOnTime extends TimerTask {

        private static final Logger LOGGER = CentralLogger.getInstance().getLogger(ProcessOnTime.class.getName());

        @Override
        public void run() {

            final LdapUpdater ldapUpdater = LdapUpdater.getInstance();
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

            } catch (final InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                LOGGER.info  ("LdapUpdater is busy" );
                LOGGER.error ("LdapUpdater is busy" );
            }
        }
    }

    /**
     * Constructor.
     * @param schedule date of the first schedule
     * @param interval recurring interval
     */
    public TimerProcessor(@Nonnull final Date schedule, final long interval) {
        _schedule = schedule;
        _interval = interval;

        execute();
    }

    /**
     * Triggers the task with the set schedule and interval
     */
    public void execute() {
        final Timer timer = new Timer();
        final TimerTask processOnTime = new ProcessOnTime();
        timer.scheduleAtFixedRate(processOnTime, _schedule, _interval);
    }
}