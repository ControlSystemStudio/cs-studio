/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**The utility class to facilitate Javascript programming.
 * @author Xihui Chen
 *
 */
public class ScriptUtil {

	/**Open an OPI.
	 * @param widget the widget to which the script is attached.
	 * @param relative_path the path of the OPI relative to the Display file of the widgetContoller.
	 * @param newWindow true if it will be opened in a new window. false if in a new tab.
	 * @param macrosInput the macrosInput. null if no macros needed.
	 */
	public final static void openOPI(AbstractBaseEditPart widget,
			String relative_path, boolean newWindow, MacrosInput macrosInput){
		IPath  path = ResourceUtil.buildAbsolutePath(
				widget.getWidgetModel(), ResourceUtil.getPathFromString(relative_path));
		RunModeService.getInstance().runOPI(path,
				newWindow ? TargetWindow.NEW_WINDOW : TargetWindow.SAME_WINDOW, null, macrosInput);
	}
	
	/**Replace current OPI with a OPI on the path relative to this OPI.
	 * @param widget the widget to which the script is attached.
	 * @param relative_path the path of the OPI relative to the OPI that is calling this method.
	 * @param macrosInput the macrosInput. null if no macros needed.
	 */
	public final static void replaceOPI(AbstractBaseEditPart widget,
			String relative_path, MacrosInput macrosInput){
		IPath  absolutePath = ResourceUtil.buildAbsolutePath(
				widget.getWidgetModel(), ResourceUtil.getPathFromString(relative_path));
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
								macrosInput));
			} catch (PartInitException e) {
				OPIBuilderPlugin.getLogger().log(Level.WARNING,
						"Failed to open " + absolutePath, e); //$NON-NLS-1$
				MessageDialog.openError(Display.getDefault()
						.getActiveShell(), "Open OPI error", NLS.bind(
						"Failed to open {0}", absolutePath));
			}

		}
	}
	

	/**Pop up an Elog dialog to make an Elog entry.
	 * @param filePath path of a file to attach or null.
	 * It could be either a local file system file path
	 * or a workspace file path. File types that the logbook support depend on
	 * implementation but should include *.gif, *.jpg: File will be attached
	 * as image.
	 */
	public final static void makeElogEntry(final String filePath){
		if(ScriptUtilSSHelper.getIMPL() != null)
			ScriptUtilSSHelper.getIMPL().makeElogEntry(filePath);
		else
			throw new RuntimeException("This method is not implemented!");
	}
	
	/**Execute an Eclipse command.
	 * @param commandId the command id.
	 */
	public static void executeEclipseCommand(String commandId){
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			    .getService(IHandlerService.class);
		
		try {
			handlerService.executeCommand(commandId, null);
		} catch (Exception e) {
			ErrorHandlerUtil.handleError("Failed to execute eclipse command: " + commandId, e);
		}
		
	}
	

}
