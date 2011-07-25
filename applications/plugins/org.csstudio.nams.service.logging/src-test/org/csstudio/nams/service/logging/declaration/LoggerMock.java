package org.csstudio.nams.service.logging.declaration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Mock of {@link ILogger}s for unit-testing.
 */
public class LoggerMock implements ILogger {

	public class LogEntry {
		private final LogType logType;
		private final Object caller;
		private final String message;
		private final Throwable throwable;

		public LogEntry(final LogType logType, final Object caller,
				final String message, final Throwable throwable) {
			super();
			this.logType = logType;
			this.caller = caller;
			this.message = message;
			this.throwable = throwable;
		}

		/**
		 * @return the caller
		 */
		public Object getCaller() {
			return this.caller;
		}

		/**
		 * @return the logType
		 */
		public LogType getLogType() {
			return this.logType;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return this.message;
		}

		/**
		 * @return the throwable
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

		@Override
		public String toString() {
			if (this.throwable != null) {
				final StringWriter str = new StringWriter();
				this.throwable.printStackTrace(new PrintWriter(str));
				return "" + this.logType + " / " + this.message + " / "
						+ this.caller + " / \n" + str.toString();
			} else {
				return "" + this.logType + " / " + this.message + " / "
						+ this.caller;
			}
		}
	}

	public enum LogType {
		DEBUG, INFO, WARNING, ERROR, FATAL
	}

	private final List<LogEntry> logEntries = new LinkedList<LogEntry>();

	public void logDebugMessage(final Object caller, final String message) {
		this.logEntries.add(new LogEntry(LogType.DEBUG, caller, message, null));
	}

	public void logDebugMessage(final Object caller, final String message,
			final Throwable throwable) {
		this.logEntries.add(new LogEntry(LogType.DEBUG, caller, message,
				throwable));
	}

	public void logErrorMessage(final Object caller, final String message) {
		this.logEntries.add(new LogEntry(LogType.ERROR, caller, message, null));
	}

	public void logErrorMessage(final Object caller, final String message,
			final Throwable throwable) {
		this.logEntries.add(new LogEntry(LogType.ERROR, caller, message,
				throwable));
	}

	public void logFatalMessage(final Object caller, final String message) {
		this.logEntries.add(new LogEntry(LogType.FATAL, caller, message, null));
	}

	public void logFatalMessage(final Object caller, final String message,
			final Throwable throwable) {
		this.logEntries.add(new LogEntry(LogType.FATAL, caller, message,
				throwable));
	}

	public void logInfoMessage(final Object caller, final String message) {
		this.logEntries.add(new LogEntry(LogType.INFO, caller, message, null));
	}

	public void logInfoMessage(final Object caller, final String message,
			final Throwable throwable) {
		this.logEntries.add(new LogEntry(LogType.INFO, caller, message,
				throwable));
	}

	public void logWarningMessage(final Object caller, final String message) {
		this.logEntries
				.add(new LogEntry(LogType.WARNING, caller, message, null));
	}

	public void logWarningMessage(final Object caller, final String message,
			final Throwable throwable) {
		this.logEntries.add(new LogEntry(LogType.WARNING, caller, message,
				throwable));
	}

	public synchronized void mockClearCurrentLogEntries() {
		this.logEntries.clear();
	}

	public synchronized LogEntry[] mockGetCurrentLogEntries() {
		return this.logEntries.toArray(new LogEntry[this.logEntries.size()]);
	}
}
