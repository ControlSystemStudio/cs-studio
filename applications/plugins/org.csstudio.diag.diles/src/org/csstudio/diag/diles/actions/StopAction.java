package org.csstudio.diag.diles.actions;

import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class StopAction extends Action implements IWorkbenchAction {

	private static final String ID = "org.csstudio.diag.diles.actions.StopAction";

	public StopAction() {
		setId(ID);
		setToolTipText("Pause a running program");
		setImageDescriptor(ImageDescriptor.createFromFile(DilesPalette.class,
				"icons/false.png"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		AutoRunAction.setRun(false);
	}

}
