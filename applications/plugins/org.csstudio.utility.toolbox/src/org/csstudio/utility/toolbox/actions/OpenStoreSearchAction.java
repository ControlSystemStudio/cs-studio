package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.StoreSearchEditorPart;

public class OpenStoreSearchAction extends AbstractSearchEditorAction<Lager> {
	
	public OpenStoreSearchAction() {
		super(StoreSearchEditorPart.ID, "Search Store");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}