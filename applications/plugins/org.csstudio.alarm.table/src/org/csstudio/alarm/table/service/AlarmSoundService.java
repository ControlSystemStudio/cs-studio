/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmSoundService.java,v 1.1 2010/04/28
 * 07:44:07 jpenning Exp $
 */
package org.csstudio.alarm.table.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javazoom.jl.player.Player;

import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
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
    
    private Map<String, String> _severityToSoundfile;
    
    // Buffer for one sound
    private final ArrayBlockingQueue<String> _queue = new ArrayBlockingQueue<String>(1);
    
    private final Thread _playerThread;
    
    public AlarmSoundService() {
        mapSeverityToSoundfile();
        _playerThread = createPlayerThread();
        _playerThread.start();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void playAlarmSound(final String severity) {
        _log.info(this, "playAlarmSound for severity " + severity);
        
        // Guard
        if (!isMappingDefinedForSeverity(severity)) {
            _log.warn(this, "No mapping defined for severity " + severity);
            return;
        }
        
        // The first caller fills the queue. As long as the sound is played, all others will pass
        // by.
        synchronized (this) {
            if (_queue.isEmpty()) {
                CentralLogger.getInstance().debug(this, "player will start");
                _queue.add(severity);
            } else {
                _log.debug(this, "playAlarmSound for " + severity + ": ignored, already playing");
            }
        }
    }
    
    private String getMp3Path(final String severity) {
        return _severityToSoundfile.get(severity);
    }
    
    private boolean isMappingDefinedForSeverity(final String severity) {
        boolean result = _severityToSoundfile.containsKey(severity);
        
        String filename = _severityToSoundfile.get(severity);
        result &= filename != null;
        result &= filename.length() > 0;
        
        return result;
    }
    
    /**
     * Read mapping of severities to colors from preferences and put mapping in a local HashMap.
     * (Performance)
     */
    private void mapSeverityToSoundfile() {
        IPreferenceStore store = new JmsLogPreferencePage().getPreferenceStore();
        _severityToSoundfile = new HashMap<String, String>();
        
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY0), store
                .getString(JmsLogPreferenceConstants.SOUND0));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY1), store
                .getString(JmsLogPreferenceConstants.SOUND1));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY2), store
                .getString(JmsLogPreferenceConstants.SOUND2));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY3), store
                .getString(JmsLogPreferenceConstants.SOUND3));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY4), store
                .getString(JmsLogPreferenceConstants.SOUND4));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY5), store
                .getString(JmsLogPreferenceConstants.SOUND5));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY6), store
                .getString(JmsLogPreferenceConstants.SOUND6));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY7), store
                .getString(JmsLogPreferenceConstants.SOUND7));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY8), store
                .getString(JmsLogPreferenceConstants.SOUND8));
        _severityToSoundfile.put(store.getString(JmsLogPreferenceConstants.KEY9), store
                .getString(JmsLogPreferenceConstants.SOUND9));
        
    }
    
    private Thread createPlayerThread() {
        return new Thread() {
            private Player _mp3Player;
            private BufferedInputStream _bufferedInputStream;
            
            @Override
            public void run() {
                while (true) {
                    try {
                        // Wait for entry
                        String severity = _queue.take();
                        CentralLogger.getInstance().debug(this, "player started");
                        
                        _bufferedInputStream = new BufferedInputStream(new FileInputStream(getMp3Path(severity)));
                        _mp3Player = new Player(_bufferedInputStream);
                        _mp3Player.play();
                    } catch (Exception e) {
                        CentralLogger.getInstance().warn(this, "player stopped on error ", e);
                    } finally {
                        CentralLogger.getInstance().debug(this, "player has finished");
                        _queue.clear();
                        try {
                            if (_bufferedInputStream != null) {
                                _bufferedInputStream.close();
                            }
                            if (_mp3Player != null) {
                                _mp3Player.close();
                            }
                        } catch (Exception e2) {
                            // can't help it
                        }
                    }
                }
            };
        };
    }
}
