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
import org.csstudio.opibuilder.commands.ChangeOrientationCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;


/**Change widget orientation.
 * @author Xihui Chen
 *
 */
public class ChangeOrientationAction extends SelectionAction {	
	
	public enum OrientationType{		
		FLIP_HORIZONTAL("Flip Horizontal", "icons/flip_horizontal.png"), //$NON-NLS-2$
		FLIP_VERTICAL("Flip Vertical", "icons/flip_vertical.png"), //$NON-NLS-2$
		ROTATE_CLOCKWISE("Rotate Right " + "90\u00b0", "icons/rotate_clockwise.png"), //$NON-NLS-2$ //$NON-NLS-3$
		ROTATE_COUNTERCLOCKWISE("Rotate Left " + "90\u00b0", "icons/rotate_anticlockwise.png");	//$NON-NLS-2$ //$NON-NLS-3$
		private String label;
		private String iconPath;
		private OrientationType(String label, String iconPath) {
			this.label = label;
			this.iconPath = iconPath;
		}
		
		public String getLabel(){
			return label;
		}
		
		public String getActionID(){
			return "org.csstudio.opibuilder.actions." + toString(); //$NON-NLS-1$
		}
		
		public ImageDescriptor getImageDescriptor(){
			return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
					OPIBuilderPlugin.PLUGIN_ID, iconPath);
		}
		
	}

	private OrientationType orientationType;

	public ChangeOrientationAction(IWorkbenchPart part, OrientationType orientationType) {
		super(part);
		setId(orientationType.getActionID());
		setImageDescriptor(orientationType.getImageDescriptor());
		setText(orientationType.getLabel());
		this.orientationType = orientationType;
	}


	@Override
	public void run() {
		CompoundCommand compoundCommand = new CompoundCommand(orientationType.getLabel());
			for(AbstractWidgetModel widgetModel : getSelectedWidgetModels()){
				compoundCommand.add(new ChangeOrientationCommand(widgetModel, orientationType));
			}
				execute(compoundCommand);
	}
	
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List<?> selection = getSelectedObjects();
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection) {
			if (o instanceof AbstractBaseEditPart) {
				selectedWidgetModels.add(
						(AbstractWidgetModel) ((EditPart) o).getModel());
			}
		}
		return selectedWidgetModels;
	}


	@Override
	protected boolean calculateEnabled() {
		List<AbstractWidgetModel> selectedWidgetModels = getSelectedWidgetModels();
		if(selectedWidgetModels.size() >= 1 &&
				!(selectedWidgetModels.get(0) instanceof DisplayModel))
			return true;
		return false;
	}
}
