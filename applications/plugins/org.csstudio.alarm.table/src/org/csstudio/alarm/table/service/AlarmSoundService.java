/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 * 
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 * 
 * $Id$
 */
package org.csstudio.alarm.table.service;

import java.util.HashMap;

import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.alarm.table.utility.Functions;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Implementation of the alarm sound service
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 27.04.2010
 */
public class AlarmSoundService implements IAlarmSoundService {
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    private HashMap<String, String> _severityColorMapping;
    
    public AlarmSoundService() {
        mapSeverityToColor();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void playAlarmSound(final String severity) {
        _log.debug(this, "playAlarmSound " + severity);
        
        if (_severityColorMapping.containsKey(severity)) {
            String mp3Path = _severityColorMapping.get(severity);
            if ( (mp3Path != null) && (!mp3Path.equals(""))) {
                _log.debug(this, "play sound file: " + mp3Path);
                Functions.playMp3(mp3Path);
            }
        } else {
            _log.warn(this, "Cannot play sound file for severity " + severity
                    + ". No mapping defined.");
        }
    }
    
    /**
     * Read mapping of severities to colors from preferences and put mapping in a local HashMap.
     * (Performance)
     */
    private void mapSeverityToColor() {
        IPreferenceStore store = new JmsLogPreferencePage().getPreferenceStore();
        _severityColorMapping = new HashMap<String, String>();
        
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY0), store
                .getString(JmsLogPreferenceConstants.SOUND0));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY1), store
                .getString(JmsLogPreferenceConstants.SOUND1));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY2), store
                .getString(JmsLogPreferenceConstants.SOUND2));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY3), store
                .getString(JmsLogPreferenceConstants.SOUND3));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY4), store
                .getString(JmsLogPreferenceConstants.SOUND4));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY5), store
                .getString(JmsLogPreferenceConstants.SOUND5));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY6), store
                .getString(JmsLogPreferenceConstants.SOUND6));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY7), store
                .getString(JmsLogPreferenceConstants.SOUND7));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY8), store
                .getString(JmsLogPreferenceConstants.SOUND8));
        _severityColorMapping.put(store.getString(JmsLogPreferenceConstants.KEY9), store
                .getString(JmsLogPreferenceConstants.SOUND9));
        
    }
    
}
