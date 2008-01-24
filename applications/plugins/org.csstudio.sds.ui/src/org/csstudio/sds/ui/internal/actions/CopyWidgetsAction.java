package org.csstudio.sds.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action class that copies all currently selected widgets to the Clipboard.
 * 
 * @author swende
 * 
 */
public final class CopyWidgetsAction extends SelectionAction {
	/**
	 * Action ID of this action.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.actions.CopyWidgetsAction";

	/**
	 * Constructor.
	 * 
	 * @param workbenchPart
	 *            a workbench part
	 */
	public CopyWidgetsAction(final IWorkbenchPart workbenchPart) {
		super(workbenchPart);
		setId(ID);
		setText("Copy");
		setActionDefinitionId("org.eclipse.ui.edit.copy");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		List<AbstractWidgetModel> selectedWidgetModels = getSelectedWidgetModels();

		if (selectedWidgetModels.size() > 0) {
			Clipboard clipboard = new Clipboard(Display.getCurrent());
			clipboard.setContents(new Object[] { selectedWidgetModels },
					new Transfer[] { WidgetModelTransfer.getInstance() });
		}
	}

	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	@SuppressWarnings("unchecked")
	private List<AbstractWidgetModel> getSelectedWidgetModels() {
		List selection = getSelectedObjects();

		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();

		for (Object o : selection) {
			if (o instanceof AbstractBaseEditPart) {
				selectedWidgetModels.add(((AbstractBaseEditPart) o)
						.getWidgetModel());
			}
		}
		return selectedWidgetModels;
	}
}
