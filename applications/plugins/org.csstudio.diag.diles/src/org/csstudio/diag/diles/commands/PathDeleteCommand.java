package org.csstudio.diag.diles.commands;

import org.csstudio.diag.diles.model.Path;
import org.eclipse.gef.commands.Command;

public class PathDeleteCommand extends Command {

	private final Path path;

	public PathDeleteCommand(Path p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		this.path = p;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		path.disconnect();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		path.reconnect();
	}
}
