package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.geometry.Point;

public class MoveBendPointCommand extends AbstractBendPointCommand {
	
	private Point _oldLocation;

	@Override
	public void execute() {
		Point point = getConnection().getBendPoints().get(getIndex());
		_oldLocation = point.getCopy();
		getConnection().moveBendPoint(getIndex(), getLocation());
	}
	
	@Override
	public void undo() {
		getConnection().moveBendPoint(getIndex(), _oldLocation);
	}

}
