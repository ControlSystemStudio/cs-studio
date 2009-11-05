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

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Add to base class the option to play an alarm sound.
 * 
 * @author jhatje
 * 
 */
public class JmsAlarmMessageReceiver extends JmsMessageReceiver {

	private boolean _playAlarmSound = true;

    HashMap<String, String> _severityColorMapping;
	
	public JmsAlarmMessageReceiver() {
		super();
		mapSeverityToColor();
	}

	@Override
	public void onMessage(Message message) {
		super.onMessage(message);
		if ((message != null) && (message instanceof MapMessage)) {
			CentralLogger.getInstance().debug(this, "alarm sound enabled: " + _playAlarmSound);
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
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault()
		.getPreferenceStore();
		String mp3Path = null;
		mp3Path = _severityColorMapping.get(newMessage.getString("SEVERITY"));
//		if (newMessage.getString("SEVERITY").equalsIgnoreCase("MAJOR")) {
//			mp3Path = preferenceStore.getString("sound_0");
//		}
//		if (newMessage.getString("SEVERITY").equalsIgnoreCase("MINOR")) {
//			mp3Path = preferenceStore.getString("sound_1");
//		}
//		if (newMessage.getString("SEVERITY").equalsIgnoreCase("INVALID")) {
//			mp3Path = preferenceStore.getString("sound_2");
//		}
		if ((mp3Path != null) && (!mp3Path.equals(""))) {
			CentralLogger.getInstance().debug(this, "play sound file: " + mp3Path);
			Functions.playMp3(mp3Path);
		}
	}

	public void setPlayAlarmSound(boolean playAlarmSound) {
		this._playAlarmSound = playAlarmSound;
	}
	
    /**
     * Read mapping of severities to colors from preferences and put mapping in
     * a local HashMap. (Performance)
     */
    private void mapSeverityToColor() {
        IPreferenceStore store = new JmsLogPreferencePage()
                .getPreferenceStore();
        //
        // if we connect to the ALARM topic - we get alarms
        // we do not have to check for the type!
        // if ((jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
        _severityColorMapping = new HashMap<String, String>();

        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY0),store
                .getString(JmsLogPreferenceConstants.SOUND0));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY1),store
        		.getString(JmsLogPreferenceConstants.SOUND1));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY2),store
        		.getString(JmsLogPreferenceConstants.SOUND2));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY3),store
        		.getString(JmsLogPreferenceConstants.SOUND3));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY4),store
        		.getString(JmsLogPreferenceConstants.SOUND4));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY5),store
        		.getString(JmsLogPreferenceConstants.SOUND5));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY6),store
        		.getString(JmsLogPreferenceConstants.SOUND6));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY7),store
        		.getString(JmsLogPreferenceConstants.SOUND7));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY8),store
        		.getString(JmsLogPreferenceConstants.SOUND8));
        _severityColorMapping.put(store
        		.getString(JmsLogPreferenceConstants.KEY9),store
        		.getString(JmsLogPreferenceConstants.SOUND9));

    }
}
