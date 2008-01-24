package org.csstudio.sds.ui.internal.actions;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Base class for actions that manipulate the current order of a widget.
 * 
 * @author Sven Wende
 * 
 */
abstract class AbstractOrderAction extends SelectionAction {

	/**
	 * Constructor.
	 * 
	 * @param workbenchPart
	 *            a workbench part
	 */
	public AbstractOrderAction(final IWorkbenchPart workbenchPart) {
		super(workbenchPart);
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
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		List selections = getSelectedObjects();

		CompoundCommand compoundCommand = new CompoundCommand(getText());

		for (Object selection : selections) {
			if (selection instanceof AbstractBaseEditPart) {
				AbstractBaseEditPart widgetEP = (AbstractBaseEditPart) selection;

				if (widgetEP.getParent() instanceof AbstractContainerEditPart) {
					ContainerModel container = ((AbstractContainerEditPart) widgetEP
							.getParent()).getContainerModel();
					Command cmd = createCommand(container, widgetEP
							.getWidgetModel());
					compoundCommand.add(cmd);
				}
			}
		}
		execute(compoundCommand);
	}
	
	/**
	 * Creates the command that manipulates the layer of the specified widget.
	 * 
	 * @param container
	 *            the widgets parent container
	 * @param widget
	 *            the widget
	 * @return the manipulating command
	 */
	protected abstract Command createCommand(final ContainerModel container,
			final AbstractWidgetModel widget);
}
