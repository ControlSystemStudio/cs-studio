package org.csstudio.logbook.ologviewer;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.msu.nscl.olog.api.Log;

public class LogInfoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection selection = HandlerUtil.getActiveMenuSelection(event);
			Log[] selectedLog = AdapterUtil.convert(selection, Log.class);
			if (selectedLog.length == 1) {
				Log log = selectedLog[0];
				Shell shell = HandlerUtil.getActiveShell(event);
				MessageDialog.openConfirm(shell, log.getId().toString(),
						log.getDescription());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
