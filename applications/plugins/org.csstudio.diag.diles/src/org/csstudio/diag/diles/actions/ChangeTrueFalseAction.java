package org.csstudio.diag.diles.actions;

import org.csstudio.diag.diles.editpart.ActivityPart;
import org.csstudio.diag.diles.editpart.CommandTrueFalsePart;
import org.csstudio.diag.diles.editpart.HardwareTrueFalsePart;
import org.csstudio.diag.diles.model.CommandTrueFalse;
import org.csstudio.diag.diles.model.HardwareTrueFalse;
import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

public class ChangeTrueFalseAction extends SelectionAction {

	public static final String ID = "org.csstudio.diag.diles.actions.OneStepAction";

	public ChangeTrueFalseAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
		setText("Change True/False");
		setToolTipText("Changes input from True to False & vice versa");
		setImageDescriptor(ImageDescriptor.createFromFile(DilesPalette.class,
				"icons/true.png"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects().size() == 1
				&& (getSelectedObjects().get(0) instanceof HardwareTrueFalsePart || getSelectedObjects()
						.get(0) instanceof CommandTrueFalsePart)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		ActivityPart activityPart = (ActivityPart) getSelectedObjects().get(0);

		if (activityPart instanceof HardwareTrueFalsePart) {
			boolean result = ((HardwareTrueFalse) activityPart.getModel())
					.getResult();
			((HardwareTrueFalse) activityPart.getModel()).setResult(!result);
			activityPart.refresh();
		} else if (activityPart instanceof CommandTrueFalsePart) {
			boolean result = ((CommandTrueFalse) activityPart.getModel())
					.getResult();
			((CommandTrueFalse) activityPart.getModel()).setResult(!result);
			activityPart.refresh();
		}

	}

}
