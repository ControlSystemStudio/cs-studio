package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * A command, which applies position and location changes to widget models.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class SetBoundsCommand extends Command {
	/**
	 * Stores the new size and location of the widget.
	 */
	private final Rectangle _newBounds;

	/**
	 * Stores the old size and location.
	 */
	private Rectangle _oldBounds;

	/**
	 * The element, whose constraints are to be changed.
	 */
	private final AbstractWidgetModel _widgetModel;

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
		_widgetModel = widgetModel;
		_newBounds = newBounds.getCopy();
		setLabel("Position und Grš§e Šndern");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		// remember old bounds
		_oldBounds = new Rectangle(new Point(_widgetModel.getX(),
				_widgetModel.getY()), new Dimension(_widgetModel.getWidth(),
				_widgetModel.getHeight()));

		doApplyBounds(_newBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		doApplyBounds(_oldBounds);
	}

	/**
	 * Applies the specified bounds to the widget model.
	 * @param bounds the bounds
	 */
	private void doApplyBounds(final Rectangle bounds) {
		// change element size
		_widgetModel.setSize(bounds.width, bounds.height);

		// change location
		_widgetModel.setLocation(bounds.x, bounds.y);
	}
}
