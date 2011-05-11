/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.jms.ISendMapMessage;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * This class gets a list of JMSMessages that should be acknowledged. The
 * acknowledge message is send to the JMS and LDAP server.
 *
 * @author jhatje
 *
 */
public final class SendAcknowledge extends Job {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(SendAcknowledge.class);

    private List<AlarmMessage> messagesToSend;
    private static String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * @param msg
     *            JMSMessage to acknowledge
     */
    private SendAcknowledge(@Nonnull final List<AlarmMessage> msg) {
        super("Send Ack"); //$NON-NLS-1$
        messagesToSend = msg;
    }

    /**
     * Creates a new job for sending acknowledgments from a collection of
     * messages to send. For each message to send, the collection must contain a
     * map of properties for that message.
     *
     * @param messages
     *            the collection of messages to send.
     * @return the <code>SendAcknowledge</code> job.
     */
    @Nonnull
    public static SendAcknowledge newFromProperties(@Nonnull
            final Collection<Map<String, String>> messages) {
        final List<AlarmMessage> messagesToSend = new ArrayList<AlarmMessage>(messages
                .size());
        for (final Map<String, String> map : messages) {
            final Set<String> keys = map.keySet();
            final String[] keyArray = keys.toArray(new String[0]);
			final AlarmMessage jmsMsg = new AlarmMessage(keyArray);
            for (final String key : keys) {
                jmsMsg.setProperty(key, map.get(key));
            }
            messagesToSend.add(jmsMsg);
        }
        return new SendAcknowledge(messagesToSend);
    }

    /**
     * Creates a new job for sending acknowledgements from a List of
     * {@link BasicMessage} to send.
     *
     * @param messages
     *            the List of JMSMessage to send.
     * @return the <code>SendAcknowledge</code> job.
     */
    @Nonnull
    public static SendAcknowledge newFromJMSMessage(@Nonnull final List<AlarmMessage> messages) {
        return new SendAcknowledge(messages);
    }

    /**
     * Sends for the list of JMSMessages an acknowledge message to the jms server.
     */
    @Override
    @Nonnull
    protected IStatus run(@Nullable final IProgressMonitor monitor) {

        final ISendMapMessage sender = JmsLogsPlugin.getDefault().getSendMapMessage();

    	try {
    		// FIXME (jpenning)  why no more startSender?
            // sender.startSender(true);

            for (final BasicMessage message : messagesToSend) {

                final SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
                final java.util.Date currentDate = new java.util.Date();
                final String time = sdf.format(currentDate);

                final MapMessage mapMessage = sender.getSessionMessageObject(AlarmMessageKey.ACK.getDefiningName());
                final Map<String, String> hm = message.getHashMap();

                for (final Entry<String, String> entry : hm.entrySet()) {
                    mapMessage.setString(entry.getKey(), entry.getValue());
                }

                // Add username and host to acknowledge message.
                final User user = SecurityFacade.getInstance().getCurrentUser();
                if (user != null) {
                    mapMessage.setString("USER", user.getUsername());
                } else {
                    mapMessage.setString("USER", "NULL");
                }
                final String host = CSSPlatformInfo.getInstance()
                        .getQualifiedHostname();
                if (host != null) {
                    mapMessage.setString(AlarmMessageKey.HOST.getDefiningName(), host);
                } else {
                    mapMessage.setString(AlarmMessageKey.HOST.getDefiningName(), "NULL");
                }

                mapMessage.setString(AlarmMessageKey.ACK.getDefiningName(), Boolean.TRUE.toString()); //$NON-NLS-1$ //$NON-NLS-2$
                mapMessage.setString("ACK_TIME", time); //$NON-NLS-1$

                // FIXME (jpenning, bknerr)
                // LDAP Write requests for acknowledging are removed.
                // Acknowledge action is no longer stored in LDAP

                if (user != null) {
                    String property = message.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName());
                    property = property == null ? "<set to null>" : property;
                    LOG.debug(user.getUsername()
                            + " send Ack message, MsgName: "
                            + message.getName()
                            + " MsgTime: " + property); //$NON-NLS-2$
                }
                sender.sendMessage(AlarmMessageKey.ACK.getDefiningName());
            }
        } catch (final Exception e) {
            JmsLogsPlugin.logException("ACK not set", e);
            return Status.CANCEL_STATUS;
        } finally {
        	// FIXME (jpenning) cleanup finally block. why no more stopSender? 
        	try {
                // sender.stopSender();
                System.out.println("stop sender!!!"); //$NON-NLS-1$
            } catch (final Exception e) {
                JmsLogsPlugin.logException("JMS Error", e);
            }
            // sender = null;

        }

        return Status.OK_STATUS;
    }

}
