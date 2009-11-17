package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**Orphan a child from its parent.
 * @author Xihui Chen
 *
 */
public class OrphanChildCommand extends Command {
	
	private AbstractContainerModel parent;
	private AbstractWidgetModel child;
	

	public OrphanChildCommand(AbstractContainerModel parent,
			AbstractWidgetModel child) {
		super("Orphan Widget");		
		this.parent = parent;
		this.child = child;
	}
	
	@Override
	public void execute() {
		parent.removeChild(child);
	}
	
	@Override
	public void undo() {
		parent.addChild(child);
	}
	
	

}
