package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.StoreEditorPart;

public class OpenStoreEditorAction extends AbstractOpenEditorAction<Lager> {

	public OpenStoreEditorAction() {
		super(StoreEditorPart.ID, "Store", Lager.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}

}
