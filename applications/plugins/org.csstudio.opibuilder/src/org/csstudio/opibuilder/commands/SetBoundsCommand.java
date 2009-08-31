 package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * A command, which applies position and location changes to widget models.
 * 
 * @author Xihui Chen
 * 
 */
public final class SetBoundsCommand extends Command {
	/**
	 * Stores the new size and location of the widget.
	 */
	private final Rectangle newBounds;

	/**
	 * Stores the old size and location.
	 */
	private Rectangle oldBounds;

	/**
	 * The element, whose constraints are to be changed.
	 */
	private final AbstractWidgetModel widgetModel;

	/**
	 * Create a command that can resize and/or move a widget model.
	 * 
	 * @param widgetModel
	 *            the widget model to manipulate
	 * @param newBounds
	 *            the new size and location
	 */
	public SetBoundsCommand(final AbstractWidgetModel widgetModel,
			final Rectangle newBounds) {
		assert widgetModel != null;
		assert newBounds != null;
		this.widgetModel = widgetModel;
		this.newBounds = newBounds.getCopy();
		setLabel("Changing widget bounds");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		// remember old bounds
		oldBounds = new Rectangle(widgetModel.getLocation(), widgetModel.getSize());

		doApplyBounds(newBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		doApplyBounds(oldBounds);
	}

	/**
	 * Applies the specified bounds to the widget model.
	 * @param bounds the bounds
	 */
	private void doApplyBounds(final Rectangle bounds) {
		// change element size
		widgetModel.setSize(bounds.width, bounds.height);

		// change location
		widgetModel.setLocation(bounds.x, bounds.y);
	}
}
