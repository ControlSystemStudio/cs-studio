package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.FirmaSearchEditorPart;

public class OpenFirmaSearchAction extends AbstractSearchEditorAction<Order> {
	
	public OpenFirmaSearchAction() {
		super(FirmaSearchEditorPart.ID, "Search Company");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}
