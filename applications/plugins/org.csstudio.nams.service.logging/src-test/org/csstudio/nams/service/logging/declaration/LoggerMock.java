package org.csstudio.nams.service.logging.declaration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Mock of {@link Logger}s for unit-testing.
 */
public class LoggerMock implements Logger {

	public enum LogType {
		DEBUG, INFO, WARNING, ERROR, FATAL
	}

	public class LogEntry {
		private LogType logType;
		private Object caller;
		private String message;
		private final Throwable throwable;

		public LogEntry(LogType logType, Object caller, String message,
				Throwable throwable) {
			super();
			this.logType = logType;
			this.caller = caller;
			this.message = message;
			this.throwable = throwable;
		}

		/**
		 * @return the logType
		 */
		public LogType getLogType() {
			return logType;
		}

		/**
		 * @return the caller
		 */
		public Object getCaller() {
			return caller;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @return the throwable
		 */
		public Throwable getThrowable() {
			return throwable;
		}

		@Override
		public String toString() {
			if (throwable != null) {
				StringWriter str = new StringWriter();
				throwable.printStackTrace(new PrintWriter(str));
				return "" + logType + " / " + message + " / " + caller + " / \n"
						+ str.toString();
			} else {
				return "" + logType + " / " + message + " / " + caller;
			}
		}
	}

	private List<LogEntry> logEntries = new LinkedList<LogEntry>();

	public synchronized LogEntry[] mockGetCurrentLogEntries() {
		return logEntries.toArray(new LogEntry[logEntries.size()]);
	}

	public synchronized void mockClearCurrentLogEntries() {
		logEntries.clear();
	}

	public void logDebugMessage(Object caller, String message) {
		logEntries.add(new LogEntry(LogType.DEBUG, caller, message, null));
	}

	public void logDebugMessage(Object caller, String message,
			Throwable throwable) {
		logEntries.add(new LogEntry(LogType.DEBUG, caller, message, throwable));
	}

	public void logErrorMessage(Object caller, String message) {
		logEntries.add(new LogEntry(LogType.ERROR, caller, message, null));
	}

	public void logErrorMessage(Object caller, String message,
			Throwable throwable) {
		logEntries.add(new LogEntry(LogType.ERROR, caller, message, throwable));
	}

	public void logFatalMessage(Object caller, String message) {
		logEntries.add(new LogEntry(LogType.FATAL, caller, message, null));
	}

	public void logFatalMessage(Object caller, String message,
			Throwable throwable) {
		logEntries.add(new LogEntry(LogType.FATAL, caller, message, throwable));
	}

	public void logInfoMessage(Object caller, String message) {
		logEntries.add(new LogEntry(LogType.INFO, caller, message, null));
	}

	public void logInfoMessage(Object caller, String message,
			Throwable throwable) {
		logEntries.add(new LogEntry(LogType.INFO, caller, message, throwable));
	}

	public void logWarningMessage(Object caller, String message) {
		logEntries.add(new LogEntry(LogType.WARNING, caller, message, null));
	}

	public void logWarningMessage(Object caller, String message,
			Throwable throwable) {
		logEntries
				.add(new LogEntry(LogType.WARNING, caller, message, throwable));
	}
}
