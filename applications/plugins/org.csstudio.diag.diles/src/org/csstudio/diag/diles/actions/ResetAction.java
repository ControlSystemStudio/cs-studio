package org.csstudio.diag.diles.actions;

import java.util.List;

import org.csstudio.diag.diles.DilesEditor;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ResetAction extends Action implements IWorkbenchAction {

	private static final String ID = "org.csstudio.diag.diles.actions.ResetAction";

	public ResetAction() {
		setId(ID);
		setToolTipText("Stop a running program");
		setImageDescriptor(ImageDescriptor.createFromFile(DilesPalette.class,
				"icons/stop.png"));
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
		List<Activity> children = DilesEditor.getChart().getChildren();

		OneStepAction.setCurrentColumn(0);

		AutoRunAction.setRun(false);
		OneStepAction.setStart(true);

		for (int j = 0; j < children.size(); j++) {
			children.get(j).setResultManually(false);
		}

		DilesEditor.getChart().changeActive();
	}

}
