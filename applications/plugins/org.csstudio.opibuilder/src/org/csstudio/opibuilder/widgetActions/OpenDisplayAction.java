/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**The action running another OPI file.
 * @author Xihui Chen
 *
 */
public class OpenDisplayAction extends AbstractWidgetAction {


	public static final String PROP_PATH = "path";//$NON-NLS-1$
	public static final String PROP_MACROS = "macros";//$NON-NLS-1$
	public static final String PROP_REPLACE = "replace";//$NON-NLS-1$
	private boolean ctrlPressed = false;
	private boolean shiftPressed = false;

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""), new String[]{"opi"}));
		addProperty(new MacrosProperty(PROP_MACROS, "Macros", WidgetPropertyCategory.Basic,
				new MacrosInput(new LinkedHashMap<String, String>(), true)));
		addProperty(new BooleanProperty(PROP_REPLACE, "Replace", WidgetPropertyCategory.Basic, true));
	}

	@Override
	public void run() {
		//read file
		IPath absolutePath = getPath();
		if(!getPath().isAbsolute()){
    		absolutePath =
    			ResourceUtil.buildAbsolutePath(getWidgetModel(), getPath());
    	}
		if(absolutePath == null)
			try {
				throw new FileNotFoundException(NLS.bind(
						"The file {0} does not exist or the file is not an OPI file in the workspace.", getPath().toString()));
			} catch (FileNotFoundException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
						e.getMessage());
				ConsoleService.getInstance().writeError(e.toString());
				return;
			}
		else {

			if(!ctrlPressed && !shiftPressed && isReplace()){
				IEditorPart activeEditor =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().getActiveEditor();
				if(activeEditor instanceof OPIRunner){
					DisplayOpenManager manager =
						(DisplayOpenManager)(activeEditor.getAdapter(DisplayOpenManager.class));
					manager.openNewDisplay();
					try {
						RunModeService.getInstance().replaceActiveEditorContent(new RunnerInput(
								absolutePath, manager, getMacrosInput()));
					} catch (PartInitException e) {
					    OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to open " + absolutePath, e); //$NON-NLS-1$
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Open file error",
								NLS.bind("Failed to open {0}", absolutePath));
					}
				}
			}else{
				TargetWindow target;
				if (shiftPressed && !ctrlPressed)
					target = TargetWindow.NEW_WINDOW;
				else
					target = TargetWindow.SAME_WINDOW;

				RunModeService.getInstance().runOPI(absolutePath, target, null, getMacrosInput(), null);
			}
		}
	}

	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}

	private MacrosInput getMacrosInput(){
		MacrosInput result = new MacrosInput(new LinkedHashMap<String, String>(), false);

		MacrosInput macrosInput = ((MacrosInput)getPropertyValue(PROP_MACROS)).getCopy();

		if(macrosInput.isInclude_parent_macros()){
			Map<String, String> macrosMap =
				getWidgetModel() instanceof AbstractContainerModel?
						((AbstractContainerModel)getWidgetModel()).getParentMacroMap() :
							getWidgetModel().getParent().getMacroMap();
			result.getMacrosMap().putAll(macrosMap);
		}
		result.getMacrosMap().putAll(macrosInput.getMacrosMap());
		return result;
	}

	/**
	 * @param ctrlPressed the ctrlPressed to set
	 */
	public final void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	/**
	 * @param shiftPressed the shiftPressed to set
	 */
	public final void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	private boolean isReplace(){
		return (Boolean)getPropertyValue(PROP_REPLACE);
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
