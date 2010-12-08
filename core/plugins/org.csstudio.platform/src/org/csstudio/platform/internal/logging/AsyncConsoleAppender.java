/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * Parts of this code are taken from the Apache Log4J code:
 * Copyright 1999,2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.csstudio.platform.internal.logging;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * <p>
 * Asnychronous appender for Log4J which logs to system out from a separate
 * thread.
 * </p>
 *
 * <p>
 * This is required to prevent a deadlock between the Log4J logger and the
 * Eclipse console view, because we want to view the system output in the
 * console view within the running CSS application. If the ConsoleAppender of
 * Log4J is used directly, a deadlock can be caused when a thread that writes a
 * log message waits in the appender for the console view to write its buffer
 * contents to the screen, the console waits for the UI thread to do that, and
 * the UI thread also wants to log something and waits for the logger.
 * </p>
 *
 * <p>
 * We implemented our own asynchronous appender because of the following bug in
 * the Log4J AsyncAppender class:
 * <a href="https://issues.apache.org/bugzilla/show_bug.cgi?id=46878">
 * https://issues.apache.org/bugzilla/show_bug.cgi?id=46878</a>
 * </p>
 *
 * @author Joerg Rathlev
 */
public class AsyncConsoleAppender extends AppenderSkeleton {

	private final ConsoleAppender _consoleAppender;
	private final BlockingQueue<LoggingEvent> _eventQueue;
	private final Dispatcher _dispatcher;
	private Thread _dispatcherThread;

	/**
	 * Creates an AsyncConsoleAppender.
	 */
	public AsyncConsoleAppender() {
		_consoleAppender = new ConsoleAppender();
		_eventQueue = new LinkedBlockingQueue<LoggingEvent>();
		_dispatcher = new Dispatcher();
		startDispatcherThread();
	}

	/**
	 * Starts the dispatcher thread.
	 */
	private void startDispatcherThread() {
		_dispatcherThread = new Thread(_dispatcher);
		_dispatcherThread.setName("AsyncConsoleAppender Dispatcher");
		_dispatcherThread.setDaemon(true);
		_dispatcherThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(final Thread t, final Throwable e) {
				LogLog.warn("AsyncConsoleAppender: dispatcher thread died, " +
						"restarting thread.", e);
				// Restart the thread
				startDispatcherThread();
			}
		});
		_dispatcherThread.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void append(final LoggingEvent event) {
		/*
		 * Calling getThreadName() makes sure that the thread name stored in the
		 * event is the current thread (which logged the event). Without this,
		 * the dispatcher thread could appear in the log output.
		 */
		event.getThreadName();

		if (!_eventQueue.offer(event)) {
			LogLog.error("Could not enqueue log message in AsyncConsoleAppender.");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void close() {
		closed = true;
		_dispatcher.stop();
		_dispatcherThread.interrupt();
		try {
			_dispatcherThread.join();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			LogLog.error("Got an InterruptedException while waiting for the "
					+ "dispatcher to finish.", e);
		}
		_consoleAppender.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activateOptions() {
		super.activateOptions();
		_consoleAppender.activateOptions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(final String name) {
		super.setName(name);
		_consoleAppender.setName(name + ".ConsoleAppender");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLayout(final Layout layout) {
		super.setLayout(layout);
		_consoleAppender.setLayout(layout);
	}

	/**
	 * @see ConsoleAppender#setTarget(String)
	 */
	public void setTarget(final String target) {
		_consoleAppender.setTarget(target);
	}

	/**
	 * @see ConsoleAppender#setFollow(boolean)
	 */
	public void setFollow(final boolean follow) {
		_consoleAppender.setFollow(follow);
	}

	/**
	 * Returns <code>true</code>. This appender requires a layout.
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Reads values from the enclosing object's queue and dispatches them to its
	 * console appender.
	 */
	private class Dispatcher implements Runnable {

		private volatile boolean _stopped = false;

		/**
		 * Stops this dispatcher.
		 */
		void stop() {
			_stopped = true;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {
			while (!_stopped) {
				try {
					final LoggingEvent event = _eventQueue.take();
					try {
						_consoleAppender.doAppend(event);
					} catch (final Exception e) {
						LogLog.warn("Exception occurred in appender [" +
								_consoleAppender.getName() + "].", e);
					}
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
