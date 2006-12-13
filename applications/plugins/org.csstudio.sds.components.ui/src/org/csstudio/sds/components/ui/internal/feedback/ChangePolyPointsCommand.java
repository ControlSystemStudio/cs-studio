package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.AbstractPolyElement;
import org.csstudio.sds.components.internal.model.PolylineElement;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;

/**
 * A command, that changes the point list of a poly element.
 * @author Sven Wende
 *
 */
public final class ChangePolyPointsCommand extends Command {
	/**
	 * The polyline element, whose points should be changed.
	 */
	private AbstractPolyElement _polyElement;

	/**
	 * The old point list.
	 */
	private PointList _oldPoints;

	/**
	 * The new point list.
	 */
	private PointList _newPoints;

	/**
	 * Constructor.
	 * @param polyElement the polyline element, whose points should be changed
	 * @param newPoints the new point list
	 */
	public ChangePolyPointsCommand(final AbstractPolyElement polyElement,
			final PointList newPoints) {
		assert polyElement != null;
		assert newPoints != null;
		_polyElement = polyElement;
		_newPoints = newPoints;
	}

	/**
	* {@inheritDoc}
	 */
	
	@Override
	public void execute() {
		_oldPoints = _polyElement.getPoints();
		_polyElement.setPoints(_newPoints);
	}

	/**
	* {@inheritDoc}
	 */
	@Override
	public void undo() {
		_polyElement.setPoints(_oldPoints);
	}

}
