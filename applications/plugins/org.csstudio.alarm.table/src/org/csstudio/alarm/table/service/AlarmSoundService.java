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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javazoom.jl.player.Player;

import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

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

    // TODO jp : refactor to enum map to {@link org.csstudio.alarm.treeView.model.Severity}
    // perhaps move the Severity definition somewhere else...
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
        _log.debug(this, "playAlarmSound for severity " + severity);

        // Guard
        if (!isMappingDefinedForSeverity(severity)) {
            _log.debug(this, "No mapping defined for severity " + severity);
            return;
        }

        if (_queue.offer(severity)) {
            _log.debug(this, "sound for severity " + severity + " has been queued");
        } else {
            _log.debug(this, "sound for severity " + severity + " has been ignored");
        }
    }

    private String getMp3Path(final String severity) {
        return _severityToSoundfile.get(severity);
    }

    private boolean isMappingDefinedForSeverity(final String severity) {
        boolean result = _severityToSoundfile.containsKey(severity);

        final String filename = _severityToSoundfile.get(severity);
        result = result && (filename != null) && (filename.length() > 0);

        return result;
    }

    /**
     * Read mapping of severities to colors from preferences and put mapping in a local HashMap.
     * (Performance)
     */
    private void mapSeverityToSoundfile() {
        final IPreferenceStore store = new JmsLogPreferencePage().getPreferenceStore();
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
        return new Thread("AlarmSoundService") {

            @Override
            public void run() {
                Player mp3Player = null;
                BufferedInputStream bufferedInputStream = null;

                while (true) {
                    try {
                        // Remove entries which occurred during previous playtime
                        _queue.clear();

                        // Wait for entry
                        final String severity = _queue.take();
                        _log.debug(this, "player started");

                        bufferedInputStream = new BufferedInputStream(getSoundStreamForSeverity(severity));
                        mp3Player = new Player(bufferedInputStream);
                        mp3Player.play();
                    } catch (final Exception e) {
                        _log.warn(this, "player stopped on error ", e);
                    } finally {
                        _log.debug(this, "player has finished");
                        try {
                            if (bufferedInputStream != null) {
                                bufferedInputStream.close();
                            }
                            if (mp3Player != null) {
                                mp3Player.close();
                            }
                        } catch (final Exception e2) {
                            _log.warn(this, "error while closing ressources", e2);
                            // can't help it
                        }
                    }
                }
            }

            /**
             * The sound ressource is located in the product bundle.
             *
             * @param severity
             * @return
             * @throws IOException
             */
            private InputStream getSoundStreamForSeverity(final String severity) throws IOException {
                final String mp3Path = getMp3Path(severity);
                final Bundle bundle = Platform.getProduct().getDefiningBundle();
                final URL url = FileLocator.find(bundle, new Path(mp3Path), null);
                final InputStream stream = url.openStream();
                return stream;
            };
        };
    }
}
