/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;

/**The action running another OPI file.
 * @author Xihui Chen
 *
 */
public class OpenOPIInViewAction extends AbstractOpenOPIAction {

	public static final String PROP_POSITION = "Position";//$NON-NLS-1$

	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new ComboProperty(PROP_POSITION, "Position", WidgetPropertyCategory.Basic,
				Position.stringValues(), 1));
	}
	
	@Override
	protected void openOPI(IPath absolutePath) {
		if(!ctrlPressed && !shiftPressed ){
			RunModeService.runOPIInView(absolutePath, null, getMacrosInput(), getPosition());
		}else{
			TargetWindow target;
			if (shiftPressed && !ctrlPressed)
				target = TargetWindow.NEW_WINDOW;
			else
				target = TargetWindow.SAME_WINDOW;

			RunModeService.getInstance().runOPI(absolutePath, target, null, getMacrosInput(), null);
		}	
	}

	protected Position getPosition(){
		return Position.values()[(Integer)getPropertyValue(PROP_POSITION)];
	}
	
	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_OPI_IN_VIEW;
	}


	@Override
	public String getDefaultDescription() {
		return "Open " + getPath();
	}

}
