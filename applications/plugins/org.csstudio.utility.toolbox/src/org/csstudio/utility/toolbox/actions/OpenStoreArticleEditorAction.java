package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.StoreArticleEditorPart;

public class OpenStoreArticleEditorAction extends AbstractOpenEditorAction<LagerArtikel> {

	public OpenStoreArticleEditorAction() {
		super(StoreArticleEditorPart.ID, "Store Article", LagerArtikel.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
}
