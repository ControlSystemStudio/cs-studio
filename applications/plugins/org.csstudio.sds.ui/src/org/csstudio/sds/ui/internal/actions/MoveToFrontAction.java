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
public final class MoveToFrontAction extends AbstractOrderAction {

	/**
	 * Action ID of this action.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.actions.MoveToFrontAction";

	/**
	 * Constructor.
	 * 
	 * @param workbenchPart
	 *            a workbench part
	 */
	public MoveToFrontAction(final IWorkbenchPart workbenchPart) {
		super(workbenchPart);
		setId(ID);
		setText("To Front");
	}

	/**
	 * {@inheritDoc}
	 */
	protected Command createCommand(final ContainerModel container,
			final AbstractWidgetModel widget) {
		return new ChangeOrderCommand(container, widget, container
				.getFrontIndex(widget));
	}
}
