package org.csstudio.utility.managementactions.actions;

import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Opens a message box displaying the value of the
 * "message" parameter passed in a string to string map.
 * @author avodovnik
 *
 */
public class ActionOpenMessage implements IAction {

	public Object run(Object param) {
		if(!(param instanceof Map)) {
			return null;
		}
		final Map paramMap = (Map)param;
		final Display d = Display.getDefault();
			
		d.asyncExec(new Runnable() {

			public void run() {
				MessageDialog.openInformation(d.getActiveShell(),
						"MGX Message", paramMap.get("message").toString());
			}
		
		});
		return null;
	}

}
