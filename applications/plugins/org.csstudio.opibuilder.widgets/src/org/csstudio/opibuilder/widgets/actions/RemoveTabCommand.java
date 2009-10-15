package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

/**The command which add a tab to the tab widget.
 * @author Xihui Chen
 *
 */
public class RemoveTabCommand extends Command {
	private int tabIndex;
	private TabEditPart tabEditPart;
	private GroupingContainerModel groupingContainer;
	private Label label;
	private boolean executed = false;
	
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
		if(tabEditPart.getWidgetModel().getChildren().size()>1){
			tabEditPart.removeTab(tabIndex);
			executed = true;
		}			
		else
			MessageDialog.openInformation(null, "Failed to Remove Tab", 
					"There must be at least one tab in the tab folder.");
			
	}
	
	@Override
	public void undo() {
		if(executed)
			tabEditPart.addTab(tabIndex, groupingContainer, label);
		executed = false;
	}
	
	
	
	
	
}
