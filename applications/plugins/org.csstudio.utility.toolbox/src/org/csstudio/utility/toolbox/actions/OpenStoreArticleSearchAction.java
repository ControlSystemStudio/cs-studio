package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.StoreArticleSearchEditorPart;

public class OpenStoreArticleSearchAction extends AbstractSearchEditorAction<LagerArtikel> {
	
	public OpenStoreArticleSearchAction() {
		super(StoreArticleSearchEditorPart.ID, "Search Article Store");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}