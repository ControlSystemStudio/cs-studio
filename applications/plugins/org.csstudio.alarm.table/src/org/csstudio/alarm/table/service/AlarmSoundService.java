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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import javazoom.jl.player.Player;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the alarm sound service.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 27.04.2010
 */
public final class AlarmSoundService implements IAlarmSoundService {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmSoundService.class);
    
	private Map<String, String> _severityToSoundfile;

	// Buffer for one sound. The String denotes the path, relative (contained in
	// bundle) or absolute, to the sound resource.
	private final ArrayBlockingQueue<String> _queue = new ArrayBlockingQueue<String>(
			1);

	private final Thread _playerThread;

	private AlarmSoundService() {
		mapSeverityToSoundfile();
		_playerThread = new PlayerThread("AlarmSoundService");
	}

	/**
	 * Read mapping of severities to sounds from preferences and put mapping in
	 * a local HashMap. (Performance)
	 */
	private void mapSeverityToSoundfile() {
		final IPreferenceStore store = new JmsLogPreferencePage()
				.getPreferenceStore();
		_severityToSoundfile = new HashMap<String, String>();

		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY0),
				store.getString(JmsLogPreferenceConstants.SOUND0));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY1),
				store.getString(JmsLogPreferenceConstants.SOUND1));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY2),
				store.getString(JmsLogPreferenceConstants.SOUND2));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY3),
				store.getString(JmsLogPreferenceConstants.SOUND3));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY4),
				store.getString(JmsLogPreferenceConstants.SOUND4));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY5),
				store.getString(JmsLogPreferenceConstants.SOUND5));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY6),
				store.getString(JmsLogPreferenceConstants.SOUND6));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY7),
				store.getString(JmsLogPreferenceConstants.SOUND7));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY8),
				store.getString(JmsLogPreferenceConstants.SOUND8));
		_severityToSoundfile.put(
				store.getString(JmsLogPreferenceConstants.KEY9),
				store.getString(JmsLogPreferenceConstants.SOUND9));

	}

	/**
	 * The alarm sound service is constructed via factory method. This is
	 * necessary to be able to start the player thread after full construction
	 * of the service.
	 * 
	 * @return the alarm sound service
	 */
	@Nonnull
	public static AlarmSoundService newAlarmSoundService() {
		// first fully construct the alarm sound service object
		final AlarmSoundService result = new AlarmSoundService();
		// then start the thread
		result.startPlayerThread();
		return result;
	}

	// Helper for the construction
	private void startPlayerThread() {
		_playerThread.start();
	}

	@Override
	public void playAlarmSound(@Nonnull final String severityAsString) {
		LOG.debug("playAlarmSound for severity {}", severityAsString);

		// Guard
		if (!isMappingDefinedForSeverity(severityAsString)) {
			LOG.debug("No mapping defined for severity {}", severityAsString);
			return;
		}

		if (_queue.offer(getMp3Path(severityAsString))) {
			LOG.debug("sound for severity {} has been queued", severityAsString);
		} else {
			LOG.debug("sound for severity {} has been ignored", severityAsString);
		}
	}

	private boolean isMappingDefinedForSeverity(@Nonnull final String severity) {
		boolean result = _severityToSoundfile.containsKey(severity);

		final String filename = _severityToSoundfile.get(severity);
		result = result && (filename != null) && (filename.length() > 0);

		return result;
	}

	@Nonnull
	private String getMp3Path(@Nonnull final String severity) {
		return _severityToSoundfile.get(severity);
	}

	@Override
	public void playAlarmSoundFromResource(@Nonnull String path) {
		if (_queue.offer(path)) {
			LOG.debug("sound from {} has been queued", path );
		} else {
			LOG.debug("sound from {} has been ignored", path );
		}
	}

	@Override
	public boolean existsResource(@Nonnull final String text) {
		boolean result = !text.isEmpty();
		if (result) {
			final Path path = new Path(text);
			if (path.isAbsolute()) {
				File file = new File(text);
				result = file.exists();
			} else {
				Bundle bundle = JmsLogsPlugin.getDefault().getBundle();
				result = bundle.getResource(text) != null;
			}
		}
		return result;
	}

	@Override
	public void reloadPreferences() {
		mapSeverityToSoundfile();
	}

	/**
	 * Class for the player thread
	 */
	private class PlayerThread extends Thread {

		public PlayerThread(@Nonnull final String name) {
			super(name);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			Player mp3Player = null;
			BufferedInputStream bufferedInputStream = null;

			while (true) {
				try {
					// Remove entries which occurred during previous playtime
					_queue.clear();

					// Wait for entry
					final String path = _queue.take();
					LOG.debug("player started");

					final InputStream soundStream = getSoundStreamForPath(path);
					if (soundStream != null) {
						bufferedInputStream = new BufferedInputStream(
								soundStream);
						mp3Player = new Player(bufferedInputStream);
						mp3Player.play();
					}
				} catch (final Exception e) {
					LOG.warn("player stopped on error ", e);
				} finally {
					LOG.debug("player has finished");
					tryToClose(mp3Player, bufferedInputStream);
				}
			}
		}

		@SuppressWarnings("synthetic-access")
		private void tryToClose(@CheckForNull final Player mp3Player,
				@CheckForNull final BufferedInputStream bufferedInputStream) {
			try {
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
				if (mp3Player != null) {
					mp3Player.close();
				}
			} catch (final Exception e2) {
				LOG.warn("error while closing resources", e2);
				// can't help it
			}
		}

		/**
		 * @param relative
		 *            or absolute path to sound resource
		 * @return the stream
		 * @throws IOException
		 */
		@CheckForNull
		private InputStream getSoundStreamForPath(@Nonnull final String mp3Path)
				throws IOException {
			final URL url = getURLFromPath(mp3Path);
			final InputStream result = url == null ? null : url.openStream();
			return result;
		}

		@CheckForNull
		private URL getURLFromPath(@Nonnull final String mp3Path) {
			URL result = null;
			Path path = new Path(mp3Path);
			if (path.isAbsolute()) {
				result = getUrlForAbsolutePath(mp3Path);
			} else {
				result = FileLocator.find(JmsLogsPlugin.getDefault()
						.getBundle(), path, null);
			}
			return result;
		}

		@CheckForNull
		private URL getUrlForAbsolutePath(@Nonnull final String mp3Path) {
			URL result = null;
			try {
				result = new File(mp3Path).toURI().toURL();
			} catch (MalformedURLException e) {
				result = null;
			}
			return result;
		}

	}
}
