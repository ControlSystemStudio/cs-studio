package de.desy.language.snl.diagram.ui.command;

public class CreateBendPointCommand extends AbstractBendPointCommand {
	
	@Override
	public void execute() {
		getConnection().getPoints().insertPoint(getLocation(), getIndex());
	}
	
	@Override
	public void undo() {
		getConnection().getPoints().removePoint(getIndex());
	}

}
