package org.csstudio.ui.menu.test;

import java.util.Arrays;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class TestPVAction implements IObjectActionDelegate {
	
	private Shell shell;
	private ISelection selection;

	public TestPVAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		MessageDialog.openInformation(
				shell,
				"PV Action",
				"PVs: " + Arrays.toString(AdapterUtil.convert(selection,
						ProcessVariable.class)));
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

}
