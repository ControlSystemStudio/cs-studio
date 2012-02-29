package org.csstudio.logbook.ologviewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class LogInfoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Shell shell = HandlerUtil.getActiveShell(event);
			MessageDialog.openConfirm(shell, "Olog info title", "Olog info detail");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
