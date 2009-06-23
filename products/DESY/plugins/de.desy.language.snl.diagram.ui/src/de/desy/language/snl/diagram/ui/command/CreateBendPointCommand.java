package de.desy.language.snl.diagram.ui.command;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Point;


public class CreateBendPointCommand extends AbstractBendPointCommand {
	
	@Override
	public void execute() {
		RelativeBendpoint bp = new RelativeBendpoint();
//		bp.set
		Point location = getLocation();
		getConnection().addBendPoint(location, getIndex());
	}
	
	@Override
	public void undo() {
		getConnection().removeBendPoint(getIndex());
	}

}
