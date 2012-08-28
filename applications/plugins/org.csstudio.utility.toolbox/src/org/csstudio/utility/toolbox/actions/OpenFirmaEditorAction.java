package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.FirmaEditorPart;

public class OpenFirmaEditorAction extends AbstractOpenEditorAction<Firma> {
	
	public OpenFirmaEditorAction() {
		super(FirmaEditorPart.ID, "Company", Firma.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}

}
