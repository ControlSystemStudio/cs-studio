package org.remotercp.common.status;

import java.io.Serializable;

import org.eclipse.core.runtime.IStatus;

/**
 * This class is used to provide status information. As the usual Status doesn't
 * implement the Serializable interface this class is used for remote methods.
 * 
 * @author Eugen Reiswich
 * 
 */
public class SerializableStatus implements IStatus, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6833486055344111124L;
	private final int severity;
	private final String pluginId;
	private final String message;
	private final Throwable exception;

	public SerializableStatus(int severity, String pluginId, String message) {
		this.severity = severity;
		this.pluginId = pluginId;
		this.message = message;
		this.exception = null;

	}

	public SerializableStatus(int severity, String pluginId, String message,
			Throwable exception) {
		this.severity = severity;
		this.pluginId = pluginId;
		this.message = message;
		this.exception = exception;
	}

	public IStatus[] getChildren() {
		return null;
	}

	public int getCode() {
		return 0;
	}

	public Throwable getException() {
		return this.exception;
	}

	public String getMessage() {
		return this.message;
	}

	public String getPlugin() {
		return this.pluginId;
	}

	public int getSeverity() {
		return this.severity;
	}

	public boolean isMultiStatus() {
		return false;
	}

	public boolean isOK() {
		return this.severity == 0;
	}

	public boolean matches(int severityMask) {
		return this.severity == severityMask;
	}

}
