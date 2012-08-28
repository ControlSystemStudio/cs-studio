package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.OrderEditorPart;

public class OpenOrderEditorAction extends AbstractOpenEditorAction<Order> {
	
	public OpenOrderEditorAction() {
		super(OrderEditorPart.ID, "Order", Order.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}
