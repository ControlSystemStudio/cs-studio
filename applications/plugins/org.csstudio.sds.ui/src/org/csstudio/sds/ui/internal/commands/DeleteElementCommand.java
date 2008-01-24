package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.Command;

/**
 * A command, that deletes an widget model from the display model.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class DeleteElementCommand extends Command {
	/**
	 * The display model.
	 */
	private ContainerModel _container;

	/**
	 * The element that gets deleted.
	 */
	private AbstractWidgetModel _child;

	/**
	 * The index of the element within the list of elements.
	 */
	private int _index;

	/**
	 * Constructor.
	 * 
	 * @param container
	 *            the display model
	 * @param child
	 *            the element, that should be deleted
	 */
	public DeleteElementCommand(final ContainerModel container,
			final AbstractWidgetModel child) {
		assert container != null;
		assert child != null;
		_container = container;
		_child = child;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_index = _container.getIndexOf(_child);
		_container.removeWidget(_child);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_container.addWidget(_index, _child);
	}

}
