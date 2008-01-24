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
package org.csstudio.sds.internal.connection.custom;

import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * Simple connection pool implementation that delivers simple channels. Each of
 * this channels is implemented as single thread that is running in the current
 * JVM.
 * 
 * @author Sven Wende
 * 
 */
public final class MultiThreadChannelPool {
	/**
	 * Marker sequence for channels that should deliver random double values.
	 */
	public static final String RANDOM_DOUBLE_MARKER = "RND:1:99:10"; //$NON-NLS-1$

	/**
	 * Marker sequence for channels that should deliver a double array (a sinus
	 * curve).
	 */
	public static final String RANDOM_DOUBLE_ARRAY_MARKER = "rWaveform"; //$NON-NLS-1$

	/**
	 * Contains all active channels.
	 */
	private ConcurrentHashMap<IProcessVariableAddress, CustomChannel> _channels;

	/**
	 * Contains all active channel threads.
	 */
	private ConcurrentHashMap<IProcessVariableAddress, IntervalThread> _threads;

	/**
	 * The singleton instance.
	 */
	private static MultiThreadChannelPool _instance;

	/**
	 * Constructor.
	 */
	private MultiThreadChannelPool() {
		_channels = new ConcurrentHashMap<IProcessVariableAddress, CustomChannel>();
		_threads = new ConcurrentHashMap<IProcessVariableAddress, IntervalThread>();
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static MultiThreadChannelPool getInstance() {
		if (_instance == null) {
			_instance = new MultiThreadChannelPool();
		}

		return _instance;
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomChannel getChannel(final IProcessVariableAddress processVariable,
			final int refreshRate) {
		assert processVariable != null;

		CustomChannel channel = null;

		// check, whether channel already exists
		if (_channels.containsKey(processVariable)) {
			channel = _channels.get(processVariable);
		} else {
			channel = new CustomChannel(processVariable);

			Runnable runnable = null;

			if (processVariable.getProperty() != null) {
				if (processVariable.getProperty().contains(
						RANDOM_DOUBLE_ARRAY_MARKER)) {
					runnable = new DoubleArrayRunnable(channel);
				} else if (processVariable.getProperty().contains(
						RANDOM_DOUBLE_MARKER)) {
					runnable = new DoubleRunnable(channel);
				} else {
					runnable = new DoubleRunnable(channel);
				}
			} else {
				runnable = new DoubleRunnable(channel);
			}

			_channels.put(processVariable, channel);

			if (runnable == null) {
				channel.setValue(Math.random() * 100);
			} else {
				_threads.put(processVariable, createAndStartThread(runnable,
						refreshRate));
			}

		}

		return channel;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroyChannel(final IProcessVariableAddress processVariable) {
		assert processVariable != null;

		CustomChannel channel = _channels.get(processVariable);

		if (channel != null) {
			final IntervalThread thread = _threads.get(processVariable);
			_threads.remove(processVariable);
			// stop the producing thread
			if (thread != null) {
				thread.stopRunning();
			}

			// destroy channel
			_channels.remove(processVariable);
		}
	}

	/**
	 * Create a thread that encapsulates the given runnable and start it.
	 * 
	 * @param runnable
	 *            The runnable.
	 * @param refreshRate
	 *            The refresh interval for the thread.
	 * 
	 * @return the created thread
	 */
	private IntervalThread createAndStartThread(final Runnable runnable,
			final int refreshRate) {
		IntervalThread thread = new IntervalThread(runnable, refreshRate);
		thread.start();
		return thread;
	}

	/**
	 * Runnable implementation that generates random double values for a certain
	 * custom channel.
	 * 
	 * @author Alexander Will
	 * @version $Revision$
	 * 
	 */
	private class DoubleRunnable implements Runnable {
		/**
		 * The channel this runnable generates data for.
		 */
		private CustomChannel _channel = null;

		/**
		 * Constructor.
		 * 
		 * @param channel
		 *            The channel this runnable generates data for.
		 */
		public DoubleRunnable(final CustomChannel channel) {
			_channel = channel;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {
			_channel.setValue(Math.random() * 100);
		}
	}

	/**
	 * Runnable implementation that generates a double array (a sinus curve) for
	 * a certain custom channel.
	 * 
	 * @author Alexander Will
	 * @version $Revision$
	 * 
	 */
	private class DoubleArrayRunnable implements Runnable {
		/**
		 * The amplitude of the generated sinus curve.
		 */
		private static final int SIN_CURVE_APLITUDE = 50;

		/**
		 * The size of the generated data array.
		 */
		private static final int ARRAY_SIZE = 1024;

		/**
		 * The channel this runnable generates data for.
		 */
		private CustomChannel _channel = null;

		/**
		 * The offset of the curve.
		 */
		private int _offset = 0;

		/**
		 * Constructor.
		 * 
		 * @param channel
		 *            The channel this runnable generates data for.
		 */
		public DoubleArrayRunnable(final CustomChannel channel) {
			_channel = channel;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {
			double[] data = new double[1024];

			double value = (Math.PI * 2) / ARRAY_SIZE;
			for (int i = 0; i < ARRAY_SIZE; i++) {
				data[i] = (Math.sin(value * (i + _offset)) * SIN_CURVE_APLITUDE);
			}
			_channel.setValue(data);
			_offset++;
		}
	}
}
