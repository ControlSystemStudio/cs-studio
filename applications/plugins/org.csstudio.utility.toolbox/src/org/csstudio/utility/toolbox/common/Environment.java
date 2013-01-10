package org.csstudio.utility.toolbox.common;


public class Environment {

	private static enum ExecutionMode {PRODUCTION, DEVELOPNENT, TEST}

	private static  ExecutionMode executionMode = ExecutionMode.PRODUCTION;

	private static boolean lastValidationFailed;

	private static boolean hadException = false;

	public static void enableTest() {
		Environment.executionMode = ExecutionMode.TEST;
	}

	public static boolean isTestMode() {
		return Environment.executionMode == ExecutionMode.TEST;
	}

	public static void setLastValidationFailed(final boolean value) {
		lastValidationFailed = value;
	}

	public static void setHadException(final boolean value) {
		hadException = value;
	}

	public static boolean isHadException() {
		return hadException;
	}

	public static boolean isLastValidationFailed() {
		return lastValidationFailed;
	}

	public String getDefaultUserName() {
		return "Moeller";
	}

	public String getDateFormat() {
		return "dd.MM.yyyy";
	}

	public String getEmptySelectionText() {
		return "(no selection)";
	}

	public String getActiveLogGroup() {
		return "KRY-";
	}
}
