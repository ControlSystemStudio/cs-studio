package org.csstudio.sds.components.ui.internal.feedback;

import org.csstudio.sds.components.internal.model.PolygonElement;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;

/**
 * A command, that changes the point list of a polygon element.
 * @author Sven Wende
 *
 */
public final class ChangePolygonPointsCommand extends Command {
	/**
	 * The polygon element, whose points should be changed.
	 */
	private PolygonElement _polygonElement;

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
	 * @param polygonElement the polygon element, whose points should be changed
	 * @param newPoints the new point list
	 */
	public ChangePolygonPointsCommand(final PolygonElement polygonElement,
			final PointList newPoints) {
		assert polygonElement != null;
		assert newPoints != null;
		_polygonElement = polygonElement;
		_newPoints = newPoints;
	}

	/**
	* {@inheritDoc}
	 */
	@Override
	public void execute() {
		_oldPoints = _polygonElement.getPoints();
		_polygonElement.setPoints(_newPoints);
	}

	/**
	* {@inheritDoc}
	 */
	@Override
	public void undo() {
		_polygonElement.setPoints(_oldPoints);
	}

}
