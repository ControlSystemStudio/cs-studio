package org.csstudio.sds.ui.internal.actions;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.internal.commands.ChangeOrderCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action class that brings the currently selected widgets to the front.
 * 
 * @author Kai Meyer
 * 
 */
public final class MoveToBackAction extends AbstractOrderAction {

	/**
	 * Action ID of this action.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.actions.MoveToBackAction";

	/**
	 * Constructor.
	 * 
	 * @param workbenchPart
	 *            a workbench part
	 */
	public MoveToBackAction(final IWorkbenchPart workbenchPart) {
		super(workbenchPart);
		setId(ID);
		setText("To Back");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(final ContainerModel container,
			final AbstractWidgetModel widget) {
		return new ChangeOrderCommand(container, widget, container
				.getBackIndex(widget));
	}
}
