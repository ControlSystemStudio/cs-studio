/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.ExecuteCommandAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgetActions.OpenOPIInViewAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * The utility class to facilitate BOY script programming.
 * 
 * @author Xihui Chen
 * 
 */
public class ScriptUtil {

	/**
	 * Open an OPI.
	 * 
	 * @param widget
	 *            the widget to which the script is attached.
	 * @param opiPath
	 *            the path of the OPI. It can be either an absolute path or a
	 *            relative path to the Display file of the widget.
	 * @param target
	 *            target place of the new OPI. 0: new tab; 1: replace current
	 *            one; 2: new window; 3: view on left; 4: view on right; 5: view
	 *            on top; 6: view on bottom; 7: detached view
	 * @param macrosInput
	 *            the macrosInput. null if no macros needed.
	 */
	public final static void openOPI(AbstractBaseEditPart widget,
			String opiPath, int target, MacrosInput macrosInput) {
		AbstractOpenOPIAction action;
		if (target < 3) {
			action = new OpenDisplayAction();
			action.setPropertyValue(OpenDisplayAction.PROP_REPLACE, target);
		} else {
			action = new OpenOPIInViewAction();
			action.setPropertyValue(OpenOPIInViewAction.PROP_POSITION,
					target - 3);
		}
		action.setWidgetModel(widget.getWidgetModel());
		action.setPropertyValue(OpenDisplayAction.PROP_PATH, opiPath);
		action.setPropertyValue(OpenDisplayAction.PROP_MACROS, macrosInput);
		action.run();
	}
	
	/**
	 * Close current active OPI.
	 */
	public static void closeCurrentOPI(){
		try {
			IWorkbenchPage activePage = 
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchPart activePart = activePage.getActivePart();
			
			if(activePart instanceof IEditorPart){
				activePage.closeEditor((IEditorPart) activePart, false);
			}else if(activePart instanceof IViewPart){
				activePage.hideView((IViewPart) activePart);
			}
		} catch (NullPointerException e) {

		}
	}
	
	
	
	/**{@link Deprecated} see {@link #makeLogbookEntry(String, String)}
	 * @param filePath
	 */
	public final static void makeElogEntry(final String filePath) {
		makeLogbookEntry("", filePath);
	}
	/**	 
	 * Pop up a logbook dialog to make a logbook entry.
	 * 
	 * @param text text of the log entry.
	 * @param filePath
	 *            path of a file to attach or null. It could be either a local
	 *            file system file path or a workspace file path. File types
	 *            that the logbook support depend on implementation but should
	 *            include *.gif, *.jpg: File will be attached as image.
	 */
	public final static void makeLogbookEntry(final String text, final String filePath) {
		if (ScriptUtilSSHelper.getIMPL() != null)
			ScriptUtilSSHelper.getIMPL().makeElogEntry(text, filePath);
		else
			throw new RuntimeException("This method is not implemented!");
	}

	/**
	 * Execute an Eclipse command.
	 * 
	 * @param commandId
	 *            the command id.
	 */
	public final static void executeEclipseCommand(String commandId) {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow()
				.getService(IHandlerService.class);

		try {
			handlerService.executeCommand(commandId, null);
		} catch (Exception e) {
			ErrorHandlerUtil.handleError("Failed to execute eclipse command: "
					+ commandId, e);
		}

	}
	
	/** Executing a system or shell command.
	 *  On Unix, that could be anything in the PATH.
	 *  <p>
	 *  Several things can happen:
	 *  <ul>
	 *  <li>Command finishes OK right away
	 *  <li>Command gives error right away
	 *  <li>Command runs for a long time, eventually giving error or OK.
	 *  </ul>
	 *  The command executor waits a little time to see if the command
	 *  finishes, and calls back in case of an error.
	 *  When the command finishes right away OK or runs longer,
	 *  we leave it be. Command output will be printed on BOY console.
	 *  
	 *  @param command Command to run. Format depends on OS.
     *  @param wait Time to wait for completion in seconds
     */
	public final static void executeSystemCommand(String command, int wait){
		ExecuteCommandAction action = new ExecuteCommandAction();
		action.setPropertyValue(ExecuteCommandAction.PROP_COMMAND, command);
		action.setPropertyValue(ExecuteCommandAction.PROP_WAIT_TIME, wait);
		action.run();
	}
	
	/**Execute a runnable in UI thread.
	 * @param runnable the runnable to be executed.
	 * @param widget any widget. It is referred to get the UI thread.
	 */
	public final static void execInUI(Runnable runnable, 
			AbstractBaseEditPart widget){
		widget.getViewer().getControl().getDisplay().asyncExec(runnable);
	}
	
	/**
	 * @return true if it the OPI is running in WebOPI.
	 */
	public final static boolean isWebOPI(){
		return OPIBuilderPlugin.isRAP();
	}
	
	/**If the current OPI is running on Mobile device. This method can only be called in UI thread.
	 * @return true if it the OPI is running in mobile device such as Android, iphone, iPad, iPod and blackberry.
	 */
	public final static boolean isMobile(){
		return OPIBuilderPlugin.isMobile(Display.getCurrent());
	}

	/**If the current OPI is running on Mobile device. This method can be called in non-UI thread.
	 * @param widget the widget on which the script is attached to. 
	 * @return true if it the OPI is running in mobile device such as Android, iphone, iPad, iPod and blackberry.
	 */
	public final static boolean isMobile(AbstractBaseEditPart widget){
		return OPIBuilderPlugin.isMobile(widget.getViewer().getControl().getDisplay());
	}
	
}
