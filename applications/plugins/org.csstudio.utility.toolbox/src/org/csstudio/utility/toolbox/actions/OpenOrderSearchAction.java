package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.OrderSearchEditorPart;

public class OpenOrderSearchAction extends AbstractSearchEditorAction<Order> {
	
	public OpenOrderSearchAction() {
		super(OrderSearchEditorPart.ID, "Search Order");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}
