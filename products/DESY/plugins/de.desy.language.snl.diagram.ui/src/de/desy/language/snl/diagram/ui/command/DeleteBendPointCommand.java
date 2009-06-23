package de.desy.language.snl.diagram.ui.command;

public class DeleteBendPointCommand extends AbstractBendPointCommand {
	
	@Override
	public void execute() {
		getConnection().getPoints().removePoint(getIndex());
	}
	
	@Override
	public void undo() {
		getConnection().getPoints().insertPoint(getLocation(), getIndex());
	}

}
