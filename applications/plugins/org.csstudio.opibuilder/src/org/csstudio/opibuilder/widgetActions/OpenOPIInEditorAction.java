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
import org.csstudio.opibuilder.properties.BooleanProperty;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The action running another OPI file.
 * 
 * @author Xihui Chen
 * 
 */
public class OpenOPIInEditorAction extends AbstractOpenOPIAction {


	public static final String PROP_REPLACE = "replace";//$NON-NLS-1$


	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new BooleanProperty(PROP_REPLACE, "Replace",
				WidgetPropertyCategory.Basic, true));
	}

	@Override
	protected void openOPI(IPath absolutePath) {
		if (!ctrlPressed && !shiftPressed && isReplace()) {				
			IWorkbenchPart activePart = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActivePart();
			if (activePart instanceof IOPIRuntime) {
				DisplayOpenManager manager = (DisplayOpenManager) (activePart
						.getAdapter(DisplayOpenManager.class));
				manager.openNewDisplay();
				try {
					RunModeService.replaceOPIRuntimeContent(
							(IOPIRuntime) activePart,
							new RunnerInput(absolutePath, manager,
									getMacrosInput()));
				} catch (PartInitException e) {
					OPIBuilderPlugin.getLogger().log(Level.WARNING,
							"Failed to open " + absolutePath, e); //$NON-NLS-1$
					MessageDialog.openError(Display.getDefault()
							.getActiveShell(), "Open file error", NLS.bind(
							"Failed to open {0}", absolutePath));
				}

			}
		} else {
			TargetWindow target;
			if (shiftPressed && !ctrlPressed)
				target = TargetWindow.NEW_WINDOW;
			else
				target = TargetWindow.SAME_WINDOW;

			RunModeService.getInstance().runOPI(absolutePath, target, null,
					getMacrosInput(), null);
		}		
	}

	private boolean isReplace() {
		return (Boolean) getPropertyValue(PROP_REPLACE);
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
