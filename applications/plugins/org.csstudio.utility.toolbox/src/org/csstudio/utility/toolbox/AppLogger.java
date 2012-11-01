package org.csstudio.utility.toolbox;

import org.csstudio.utility.toolbox.common.Constant;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.inject.Singleton;

@Singleton
public class AppLogger {
		
	public void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}
	
	public void logError(Throwable exception) {
		logError("Unexpected exception", exception);		
	}

	public void logError(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.OK, message, exception);
	}

	private void log(int severity, int code, String message, Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}
	
	private IStatus createStatus(int severity, int code, String message, Throwable exception) {
		return new Status(severity, Constant.PLUGIN_ID, code, message, exception);
	}
	
	private void log (IStatus status) {
		ToolboxPlugin.getDefault().getLog().log(status);
	}
}
