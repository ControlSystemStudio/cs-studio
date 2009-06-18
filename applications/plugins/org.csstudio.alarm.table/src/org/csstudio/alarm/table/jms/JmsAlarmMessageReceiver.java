/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.preferences.AlarmViewPreferenceConstants;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Add to base class the option to play an alarm sound.
 * 
 * @author jhatje
 * 
 */
public class JmsAlarmMessageReceiver extends JmsMessageReceiver {

	private boolean _playAlarmSound = true;

	public JmsAlarmMessageReceiver(MessageList messageList) {
		super(messageList);
	}

	@Override
	public void onMessage(Message message) {
		super.onMessage(message);
		if ((message != null) && (message instanceof MapMessage)) {
			if (_playAlarmSound) {
				try {
					playAlarmSound((MapMessage) message);
				} catch (Exception e) {
					CentralLogger.getInstance().error(this,
							"Error playing alarm sound " + e.toString());
				}
			}
		} else {
			CentralLogger.getInstance().error(this, "invalid message format");
		}
	}

	/**
	 * Plays a mp3 file set in the preferences if a alarm message with severity
	 * MAJOR is received.
	 * 
	 * @param newMessage
	 */
	private void playAlarmSound(MapMessage newMessage) throws JMSException {
		if (newMessage.getString("SEVERITY").equalsIgnoreCase("MAJOR")) {
			String mp3Path = JmsLogsPlugin
					.getDefault()
					.getPluginPreferences()
					.getString(
							AlarmViewPreferenceConstants.LOG_ALARM_SOUND_FILE);
			if ((mp3Path != null) && (!mp3Path.equals(""))) {
				Functions.playMp3(mp3Path);
			}
		}
	}

	public void setPlayAlarmSound(boolean playAlarmSound) {
		this._playAlarmSound = playAlarmSound;
	}
}
