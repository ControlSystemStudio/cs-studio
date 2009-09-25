package org.csstudio.diag.diles.commands;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.diag.diles.model.AbstractChartElement;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Chart;
import org.csstudio.diag.diles.model.Path;
import org.eclipse.gef.commands.Command;

public class DeleteCommand extends Command {
	private Chart parent;
	private Activity child;
	private List sourceConnections = new ArrayList();
	private List targetConnections = new ArrayList();

	private void deleteConnections(AbstractChartElement a) {
		if (a instanceof Chart) {
			List children = ((Chart) a).getChildren();
			for (int i = 0; i < children.size(); i++)
				deleteConnections((Activity) children.get(i));
		} else {
			sourceConnections.addAll(((Activity) a).getSourceConnections());
			for (int i = 0; i < sourceConnections.size(); i++) {
				Path path = (Path) sourceConnections.get(i);
				path.detachSource();
				path.detachTarget();
			}
			targetConnections.addAll(((Activity) a).getTargetConnections());
			for (int i = 0; i < targetConnections.size(); i++) {
				Path path = (Path) targetConnections.get(i);
				path.detachSource();
				path.detachTarget();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		deleteConnections(child);
		parent.removeChild(child);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	private void restoreConnections() {
		for (int i = 0; i < sourceConnections.size(); i++) {
			Path path = (Path) sourceConnections.get(i);
			path.getTarget().addTargetConnection(path);
			path.getSource().addSourceConnection(path);
		}
		sourceConnections.clear();
		for (int i = 0; i < targetConnections.size(); i++) {
			Path path = (Path) targetConnections.get(i);
			path.getSource().addSourceConnection(path);
			path.getTarget().addTargetConnection(path);
		}
		targetConnections.clear();
	}

	public void setChild(Activity a) {
		child = a;
	}

	public void setParent(Chart c) {
		parent = c;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.addChild(child);
		restoreConnections();
	}
}
