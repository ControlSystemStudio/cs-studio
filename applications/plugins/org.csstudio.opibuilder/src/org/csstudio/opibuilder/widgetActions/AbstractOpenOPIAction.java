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

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * The abstract action opening an OPI file. It can be subclassed to be opened in view or editor.
 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractOpenOPIAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$
	public static final String PROP_MACROS = "macros";//$NON-NLS-1$
	protected boolean ctrlPressed = false;
	protected boolean shiftPressed = false;

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(PROP_PATH, "File Path",
				WidgetPropertyCategory.Basic, new Path(""),
				new String[] { "opi" }, false)); //$NON-NLS-1$
		addProperty(new MacrosProperty(PROP_MACROS, "Macros",
				WidgetPropertyCategory.Basic, new MacrosInput(
						new LinkedHashMap<String, String>(), true)));
	}

	@Override
	public void run() {
		// read file
		IPath absolutePath = getPath();
		if (!getPath().isAbsolute()) {
			absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(),
					getPath());		
			if(!ResourceUtil.isExsitingFile(absolutePath, true)){
				//search from OPI search path
				absolutePath = ResourceUtil.getFileOnSearchPath(getPath(), true);
			}
		}
		if (absolutePath == null)
			try {
				throw new FileNotFoundException(
						NLS.bind(
								"The file {0} does not exist.",
								getPath().toString()));
			} catch (FileNotFoundException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"File Open Error", e.getMessage());
				ConsoleService.getInstance().writeError(e.toString());
				return;
			}
		
		openOPI(absolutePath);
		
	}

	abstract protected void openOPI(IPath absolutePath);

	protected IPath getPath() {
		return (IPath) getPropertyValue(PROP_PATH);
	}

	protected MacrosInput getMacrosInput() {
		MacrosInput result = new MacrosInput(
				new LinkedHashMap<String, String>(), false);

		MacrosInput macrosInput = ((MacrosInput) getPropertyValue(PROP_MACROS))
				.getCopy();

		if (macrosInput.isInclude_parent_macros()) {
			Map<String, String> macrosMap = getWidgetModel() instanceof AbstractContainerModel ? ((AbstractContainerModel) getWidgetModel())
					.getParentMacroMap() : getWidgetModel().getParent()
					.getMacroMap();
			result.getMacrosMap().putAll(macrosMap);
		}
		result.getMacrosMap().putAll(macrosInput.getMacrosMap());
		return result;
	}

	/**
	 * @param ctrlPressed
	 *            the ctrlPressed to set
	 */
	public final void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	/**
	 * @param shiftPressed
	 *            the shiftPressed to set
	 */
	public final void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}
	

	@Override
	public String getDefaultDescription() {
		return "Open " + getPath();
	}

}
