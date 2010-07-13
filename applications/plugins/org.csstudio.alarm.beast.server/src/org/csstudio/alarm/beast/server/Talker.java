/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import javax.jms.MapMessage;

import org.csstudio.alarm.beast.JMSAlarmCommunicator;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;

/** Talker sends messages to a JMS topic which supposedly interfaces to an
 *  annunciation system.
 *   
 *  @author Kay Kasemir
 */
public class Talker extends JMSAlarmCommunicator
{
    /** TYPE identifier used for talk messages */
    private static final String TYPE_TALK = "talk"; //$NON-NLS-1$

    /** Initialize
     *  @throws Exception on error
     */
    public Talker() throws Exception
    {
        super(Preferences.getAlarmTreeRoot(),
              Preferences.getJMS_TalkTopic(), null, false);
    }
    
    /** Send message to annunciator
     *  @param message Message text
     */
    public void say(final String message)
    {
        say (null, message);
    }
    
    /** Send message to annunciator
     *  @param level Severity Level or <code>null</code> 
     *  @param message Message text
     */
    public void say(final SeverityLevel level, final String message)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    final MapMessage map = createBasicMapMessage(
                            Application.APPLICATION_NAME, TYPE_TALK, message);
                    if (level != null)
                        map.setString(JMSLogMessage.SEVERITY, level.name());
                    producer.send(map);
                    return;
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error(
                            "Error while sending " + message, ex); //$NON-NLS-1$
                }
            }
        });
    }
}
