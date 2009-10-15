package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;

/**The command which add a tab to the tab widget.
 * @author Xihui Chen
 *
 */
public class RemoveTabCommand extends Command {
	private int tabIndex;
	private TabEditPart tabEditPart;
	private GroupingContainerModel groupingContainer;
	private Label label;
	
	public RemoveTabCommand(TabEditPart tabEditPart) {
		this.tabEditPart = tabEditPart;
		this.tabIndex = tabEditPart.getActiveTabIndex();
		this.groupingContainer = tabEditPart.getGroupingContainer(tabIndex);
		this.label = new Label();
		Label oldLabel = tabEditPart.getTabLabel(tabIndex);
		label.setText(oldLabel.getText());
		label.setBackgroundColor(oldLabel.getBackgroundColor());
		label.setForegroundColor(oldLabel.getForegroundColor());
		label.setFont(oldLabel.getFont());
		setLabel("Remove Tab");
	}
	
	@Override
	public void execute() {
		tabEditPart.removeTab(tabIndex);
	}
	
	@Override
	public void undo() {
		tabEditPart.addTab(tabIndex, groupingContainer, label);
	}
	
	
	
	
	
}
