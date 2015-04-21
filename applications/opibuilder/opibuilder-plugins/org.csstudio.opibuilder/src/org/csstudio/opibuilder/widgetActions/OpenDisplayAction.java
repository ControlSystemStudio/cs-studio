/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.jdom.Element;

/**
 * The action running another OPI file.
 * 
 * @author Xihui Chen
 * 
 */
public class OpenDisplayAction extends AbstractOpenOPIAction {


	public static final String PROP_REPLACE = "replace";//$NON-NLS-1$

	private enum OpenDisplayTarget{
		NEW_TAB("Open in new Tab"),
		REPLACE("Replace"),		
		NEW_WINDOW("Open in new Window");
		
		private String description;
		private OpenDisplayTarget(String desc) {
			this.description = desc;
		}
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(OpenDisplayTarget p : values())
				sv[i++] = p.description;
			return sv;
		}
	}
	

	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new ComboProperty(PROP_REPLACE, "Target",
				WidgetPropertyCategory.Basic, OpenDisplayTarget.stringValues(), 1){
			@Override
			public Object readValueFromXML(Element propElement) {
				try {
					Integer index = Integer.parseInt(propElement.getValue());
					return index;
				} catch (NumberFormatException e) {
					boolean b = Boolean.parseBoolean(propElement.getValue());
					return b?new Integer(1): new Integer(0);
				}
			}
		});
	}

	@Override
	protected void openOPI(IPath absolutePath) {
		if (!ctrlPressed && !shiftPressed && getOpenDisplayTarget() == OpenDisplayTarget.REPLACE) {				
			IOPIRuntime opiRuntime = getWidgetModel().getRootDisplayModel()
					.getOpiRuntime();
			DisplayOpenManager manager = (DisplayOpenManager) (opiRuntime
					.getAdapter(DisplayOpenManager.class));
			manager.openNewDisplay();
			try {
				RunModeService
						.replaceOPIRuntimeContent(opiRuntime, new RunnerInput(
								absolutePath, manager, getMacrosInput()));
			} catch (PartInitException e) {
				OPIBuilderPlugin.getLogger().log(Level.WARNING,
						"Failed to open " + absolutePath, e); //$NON-NLS-1$
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Open file error",
						NLS.bind("Failed to open {0}", absolutePath));
			}
			
		} else {
			TargetWindow target;
			if(!ctrlPressed && !shiftPressed){
				switch (getOpenDisplayTarget()) {
				case NEW_TAB:
					target = TargetWindow.SAME_WINDOW;
					break;
				case NEW_WINDOW:
					target = TargetWindow.NEW_WINDOW;
					break;
				default:
					target = TargetWindow.SAME_WINDOW;
					break;
				}
			}else if (shiftPressed && !ctrlPressed)
				target = TargetWindow.NEW_WINDOW;
			else
				target = TargetWindow.SAME_WINDOW;

			RunModeService.getInstance().runOPI(absolutePath, target, null,
					getMacrosInput(), null);
		}		
	}

	private OpenDisplayTarget getOpenDisplayTarget() {
		int index = (Integer) getPropertyValue(PROP_REPLACE);
		return OpenDisplayTarget.values()[index];
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_DISPLAY;
	}

	@Override
	public String getDefaultDescription() {
		return "Open " + getPath();
	}

}
