package org.csstudio.diag.diles.commands;

import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Chart;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class CreateCommand extends Command {
	private Chart parent;
	private Activity child;
	private Rectangle rect;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (rect != null) {
			child.setLocation(rect.getLocation());
			if (!rect.isEmpty())
				child.setSize(rect.getSize());
		}
		parent.addChild(child);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	public void setChild(Activity activity) {
		child = activity;
	}

	public void setConstraint(Rectangle bounds) {
		rect = bounds;
	}

	public void setParent(Chart sa) {
		parent = sa;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.removeChild(child);
	}
}