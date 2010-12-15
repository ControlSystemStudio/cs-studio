package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.csstudio.opibuilder.widgets.editparts.TabItem;
import org.csstudio.opibuilder.widgets.model.TabModel;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**Duplicate a tab
 * @author Xihui Chen
 *
 */
public class DuplicateTabCommand extends Command {
	private int tabIndex;
	private TabModel tabModel;
	private TabItem tabItem;
	
	public DuplicateTabCommand(TabEditPart tabEditPart) {
		this.tabModel = tabEditPart.getWidgetModel();
		this.tabIndex = tabEditPart.getActiveTabIndex()+1;
		try {
			this.tabItem = tabEditPart.getTabItem(tabIndex -1).getCopy();
		} catch (Exception e) {
			String message = NLS.bind("Failed to duplicate this tab. \n {0}", e);
			CentralLogger.getInstance().error(this, message, e);
			ConsoleService.getInstance().writeError(message);
		}		
		setLabel("Duplicate Tab");
	}
	
	@Override
	public void execute() {
		tabModel.addTab(tabIndex, tabItem);
	}
	
	@Override
	public void undo() {
		tabModel.removeTab(tabIndex);
	}
	
	
	
	
	
}
