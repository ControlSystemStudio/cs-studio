/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.commands.ReplaceWidgetCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.opibuilder.visualparts.WidgetsSelectDialog;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;

/**The action that paste the properties from clipboard.
 * @author Xihui Chen
 *
 */
public class ReplaceWidgetsAction extends SelectionAction {

	public static final String ID = "org.csstudio.opibuilder.actions.replaceWidgets";	//$NON-NLS-1$
	
	
	public ReplaceWidgetsAction(IWorkbenchPart part) {
		super(part);
		setText("Replace Widgets With...");
		setId(ID);
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/replace.png")); //$NON-NLS-1$
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedWidgetModels().size() >0 &&
				!(getSelectedWidgetModels().get(0) instanceof DisplayModel))
			return true;
		return false;
	}
	
	
	public Command createReplaceWidgetCommand(String typeID){
		CompoundCommand cmd = new CompoundCommand("Replace widgets");
		
		for(AbstractWidgetModel targetWidget : getSelectedWidgetModels()){		
			AbstractWidgetModel widgetModel = WidgetsService.getInstance().
					getWidgetDescriptor(typeID).getWidgetModel();	
			for(String prop_id : targetWidget.getAllPropertyIDs()){
				if(widgetModel.getProperty(prop_id) == null || 
						prop_id.equals(AbstractWidgetModel.PROP_WIDGET_TYPE))
					continue;				
				widgetModel.setPropertyValue(prop_id, targetWidget.getPropertyValue(prop_id));				
			}
			cmd.add(new ReplaceWidgetCommand(targetWidget.getParent(), targetWidget, widgetModel));					
		}
		
		return cmd;
		
	}
	
	
	@Override
	public void run() {
		WidgetsSelectDialog dialog = new WidgetsSelectDialog(
				getWorkbenchPart().getSite().getShell(), 1, false);		
		dialog.setDefaultSelectedWidgetID("org.csstudio.opibuilder.widgets.NativeText"); //$NON-NLS-1$
		if(dialog.open() == Window.OK){
			String typeID = dialog.getOutput();
			execute(createReplaceWidgetCommand(typeID));
		}
	}	
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List<?> selection = getSelectedObjects();
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection) {
			if (o instanceof EditPart) {
				selectedWidgetModels.add((AbstractWidgetModel) ((EditPart) o).getModel());
			}
		}
		return selectedWidgetModels;
	}
}
