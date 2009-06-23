package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.geometry.Point;

public class MoveBendPointCommand extends AbstractBendPointCommand {
	
	private Point _oldLocation;

	@Override
	public void execute() {
		Point point = getConnection().getPoints().getPoint(getIndex());
		_oldLocation = point.getCopy();
		point.setLocation(getLocation());
	}
	
	@Override
	public void undo() {
		getConnection().getPoints().getPoint(getIndex()).setLocation(_oldLocation);
	}

}
