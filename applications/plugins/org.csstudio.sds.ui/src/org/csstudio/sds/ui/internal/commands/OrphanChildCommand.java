package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.Command;

/**
 * A command, to remove a widget model from a model.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public class OrphanChildCommand extends Command {
	/**
	 * The model.
	 */
	private ContainerModel _container;

	/**
	 * The widget model.
	 */
	private AbstractWidgetModel _widget;

	/**
	 * Standard constructor.
	 * 
	 * @param container
	 *            The model.
	 * @param widget
	 *            The widget model.
	 */
	public OrphanChildCommand(final ContainerModel container,
			final AbstractWidgetModel widget) {
		super("Orphan Child");
		_container = container;
		_widget = widget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void execute() {
		_container.removeWidget(_widget);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void undo() {
		_container.addWidget(_widget);
	}

}
