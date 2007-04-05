package org.csstudio.utility.managementactions.actions;

import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Implements the &quot;Send message&quot; management action. The
 * message received from the remote CSS instance is displayed to the
 * user by this action.
 * 
 * @author Anze Vodovnik, Joerg Rathlev
 */
public class ActionOpenMessage implements IAction {
	
	private final CentralLogger log = CentralLogger.getInstance();

	/**
	 * Runs this action.
	 * 
	 * @param param the parameter object received from the remote CSS
	 *        instance. This must be a {@code Map<String, String>} which
	 *        must contain the message to be displayed under the key
	 *        &quot;{@code Message}&quot;.
	 * @return {@code null}.
	 */
	public Object run(Object param) {
		if(!(param instanceof Map)) {
			log.warn(this, "Parameter object is not of type Map.");
			return null;
		}
		final Object msgObject = ((Map) param).get("Message");
		if (!(msgObject instanceof String)) {
			log.warn(this, "Parameters do not contain a message.");
			return null;
		}
		final String message = (String) msgObject;

		final Display d = Display.getDefault();
		d.asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(d.getActiveShell(),
						"Management Message", message);
			}
		});
		return null;
	}

}
