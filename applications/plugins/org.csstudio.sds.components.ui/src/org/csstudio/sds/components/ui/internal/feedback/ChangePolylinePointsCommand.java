package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PolylineElement;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;

/**
 * A command, that changes the point list of a polyline element.
 * @author Sven Wende
 *
 */
public final class ChangePolylinePointsCommand extends Command {
	/**
	 * The polyline element, whose points should be changed.
	 */
	private PolylineElement _polylineElement;

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
	 * @param polylineElement the polyline element, whose points should be changed
	 * @param newPoints the new point list
	 */
	public ChangePolylinePointsCommand(final PolylineElement polylineElement,
			final PointList newPoints) {
		assert polylineElement != null;
		assert newPoints != null;
		_polylineElement = polylineElement;
		_newPoints = newPoints;
	}

	/**
	* {@inheritDoc}
	 */
	
	@Override
	public void execute() {
		_oldPoints = _polylineElement.getPoints();
		_polylineElement.setPoints(_newPoints);
	}

	/**
	* {@inheritDoc}
	 */
	@Override
	public void undo() {
		_polylineElement.setPoints(_oldPoints);
	}

}
