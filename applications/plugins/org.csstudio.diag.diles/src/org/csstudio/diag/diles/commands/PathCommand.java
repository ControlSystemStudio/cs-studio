package org.csstudio.diag.diles.commands;

import java.util.Iterator;
import java.util.Vector;

import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Path;
import org.eclipse.gef.commands.Command;

public class PathCommand extends Command {
	protected Activity oldSource, source;
	protected Activity oldTarget, target;
	protected String oldSourceName, sourceName;
	protected String oldTargetName, targetName;
	protected Path path;

	public PathCommand() {
	}

	/**
	 * Makes sure there is only one connection per input terminal.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		if (target != null) {
			Vector conns = target.getConnections();
			Iterator i = conns.iterator();
			while (i.hasNext()) {
				Path conn = (Path) i.next();
				if (targetName != null && conn.getTargetName() != null)
					if (conn.getTargetName().equals(targetName)
							&& conn.getTarget().equals(target))
						return false;
			}
		}

		if (source == target) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (source != null) {
			path.detachSource();
			path.setSource(source);
			path.setSourceName(sourceName);
			path.setSourceId(source.getUniqueId());
			path.attachSource();
		}
		if (target != null) {
			path.detachTarget();
			path.setTarget(target);
			path.setTargetName(targetName);
			path.setTargetId(target.getUniqueId());
			path.attachTarget();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	public void setPath(Path p) {
		path = p;
		oldSource = p.getSource();
		oldTarget = p.getTarget();
		oldSourceName = p.getSourceName();
		oldTargetName = p.getTargetName();
	}

	public void setSource(Activity newSource) {
		source = newSource;
	}

	public void setSourceName(String newSourceName) {
		sourceName = newSourceName;
	}

	public void setTarget(Activity newTarget) {
		target = newTarget;
	}

	public void setTargetName(String newTargetName) {
		targetName = newTargetName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		source = path.getSource();
		target = path.getTarget();
		sourceName = path.getSourceName();
		targetName = path.getTargetName();
		path.detachSource();
		path.detachTarget();
		path.setSource(oldSource);
		path.setTarget(oldTarget);
		path.setSourceName(oldSourceName);
		path.setTargetName(oldTargetName);
		path.attachSource();
		path.attachTarget();
	}
}