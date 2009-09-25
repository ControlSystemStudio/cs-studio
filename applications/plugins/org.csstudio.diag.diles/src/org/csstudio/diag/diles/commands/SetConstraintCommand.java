package org.csstudio.diag.diles.commands;

import org.csstudio.diag.diles.model.Activity;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class SetConstraintCommand extends org.eclipse.gef.commands.Command {
	private Point oldPos, newPos;
	private Dimension oldSize, newSize;
	private Activity act;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldSize = act.getSize();
		oldPos = act.getLocation();
		act.setLocation(newPos);
		act.setSize(newSize);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		act.setSize(newSize);
		act.setLocation(newPos);
	}

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
		setSize(r.getSize());
	}

	public void setPart(Activity part) {
		this.act = part;
	}

	public void setSize(Dimension p) {
		newSize = p;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		act.setSize(oldSize);
		act.setLocation(oldPos);
	}
}