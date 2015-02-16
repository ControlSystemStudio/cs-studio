package org.csstudio.ui.menu.test;

import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class TestPVCommandHandler extends AbstractAdaptedHandler<ProcessVariable> {

	public TestPVCommandHandler() {
		super(ProcessVariable.class);
	}
	
	@Override
	protected void execute(List<ProcessVariable> data, ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
		window.getShell(),
		"PV Command",
		"PVs: " + data);
	}

}
