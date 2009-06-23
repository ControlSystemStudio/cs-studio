package de.desy.language.snl.diagram.ui.command;

public class DeleteBendPointCommand extends AbstractBendPointCommand {
	
	@Override
	public void execute() {
		getConnection().removeBendPoint(getIndex());
	}
	
	@Override
	public void undo() {
		getConnection().addBendPoint(getLocation(), getIndex());
	}

}
