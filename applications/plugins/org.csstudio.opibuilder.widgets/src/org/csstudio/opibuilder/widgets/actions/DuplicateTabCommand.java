package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**Duplicate a tab
 * @author Xihui Chen
 *
 */
public class DuplicateTabCommand extends Command {
	private int tabIndex;
	private TabEditPart tabEditPart;
	private GroupingContainerModel groupingContainer;
	private Label label;
	
	public DuplicateTabCommand(TabEditPart tabEditPart) {
		this.tabEditPart = tabEditPart;
		this.tabIndex = tabEditPart.getActiveTabIndex()+1;
		String xmlString = XMLUtil.WidgetToXMLString(tabEditPart.getGroupingContainer(tabIndex-1), false);
		try {
			this.groupingContainer = (GroupingContainerModel) XMLUtil.XMLStringToWidget(xmlString);
		} catch (Exception e) {
			String message = NLS.bind("Failed to duplicate this tab. \n {0}", e);
			CentralLogger.getInstance().error(this, message, e);
			ConsoleService.getInstance().writeError(message);
		}
		this.label = new Label();
		Label oldLabel = tabEditPart.getTabLabel(tabIndex-1);
		label.setText(oldLabel.getText());
		label.setBackgroundColor(oldLabel.getBackgroundColor());
		label.setForegroundColor(oldLabel.getForegroundColor());
		label.setFont(oldLabel.getFont());
		setLabel("Duplicate Tab");
	}
	
	@Override
	public void execute() {
		tabEditPart.addTab(tabIndex, groupingContainer, label);
	}
	
	@Override
	public void undo() {
		tabEditPart.removeTab(tabIndex);
	}
	
	
	
	
	
}
