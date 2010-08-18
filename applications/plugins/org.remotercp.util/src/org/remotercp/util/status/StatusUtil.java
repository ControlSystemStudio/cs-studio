package org.remotercp.util.status;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class StatusUtil {

	/**
	 * This method checks the provided status list for errors and warnings. The
	 * result is the most severe problem found in the given list.
	 * 
	 * @param statusList
	 *            List with {@link IStatus} objects
	 * @return The most severe problem if found otherwise Status.OK
	 */
	public static int checkStatus(List<IStatus> statusList) {
		int severity = Status.OK;

		for (IStatus status : statusList) {
			if (status.getSeverity() == Status.ERROR) {
				return Status.ERROR;
			}
			if(status.getSeverity() == Status.CANCEL){
				return Status.CANCEL;
			}
			if (status.getSeverity() == Status.WARNING) {
				severity = Status.WARNING;
			}
			if (status.getSeverity() == Status.OK) {
				// change to ok status only if no warnings found
				if (severity != Status.WARNING) {
					severity = Status.OK;
				}
			}
		}
		return severity;
	}
}
