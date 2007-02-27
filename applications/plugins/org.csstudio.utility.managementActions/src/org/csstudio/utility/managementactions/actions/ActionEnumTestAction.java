package org.csstudio.utility.managementactions.actions;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class ActionEnumTestAction implements IAction {

	public Object run(Object param) {
		Display d = Display.getCurrent();
		if(d == null)
			d = new Display();
			
		MessageDialog.openInformation(d.getActiveShell(),
				"Enum Test", param.toString());
		return null;
	}

}
