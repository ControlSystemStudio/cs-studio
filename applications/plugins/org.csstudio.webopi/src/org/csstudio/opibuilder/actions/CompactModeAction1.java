/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchWindow;


/**The action to make CSS full screen.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class CompactModeAction1 extends WorkbenchPartAction implements 
	IWorkbenchWindowActionDelegate{
	
	private static final String COMPACT_MODE = "Compact Mode";

	private static final String EXIT_COMPACT_MODE = "Exit Compact Mode";

	public static final String ID = "org.csstudio.opibuilder.actions.compactMode"; //$NON-NLS-1$
	
	private ActionFactory.IWorkbenchAction toggleToolbarAction;
	private Menu menuBar;
	private boolean inFullScreenMode = false;
	private Shell shell;
	private ImageDescriptor fullScreenImage = 
		CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			OPIBuilderPlugin.PLUGIN_ID, "icons/compact_mode.png");
	private ImageDescriptor exitFullScreenImage = 
		CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			OPIBuilderPlugin.PLUGIN_ID, "icons/exit_compact_mode.gif");
	private IWorkbenchWindow window;
	private boolean toolbarWasInvisible;
	
	
	
	/**
	 * Constructor.
	 * @param part The workbench part associated with this PrintAction
	 */
	public CompactModeAction1(IWorkbenchPart part) {
		super(part);
		setActionDefinitionId(ID);
		window = part.getSite().getWorkbenchWindow();
		 toggleToolbarAction = ActionFactory.TOGGLE_COOLBAR.create(window); 
		 shell = part.getSite().getWorkbenchWindow().getShell();
		 menuBar = shell.getMenuBar();
		if(window instanceof WorkbenchWindow && !((WorkbenchWindow) window).getCoolBarVisible()){
			inFullScreenMode = true;
			shell.setMenuBar(null);	
			setImageDescriptor(exitFullScreenImage);
			setText(EXIT_COMPACT_MODE);

		}else{
			setText(COMPACT_MODE);
			setImageDescriptor(fullScreenImage);
		}
		 
	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
	 */
	protected void init() {
		super.init();
		setId(ID);
		}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if(inFullScreenMode){
			if(!toolbarWasInvisible)
				toggleToolbarAction.run();
			shell.setMenuBar(menuBar);		
			inFullScreenMode = false;
			setText(COMPACT_MODE);
			setImageDescriptor(fullScreenImage);
		}else {
			if(window instanceof WorkbenchWindow && !((WorkbenchWindow) window).getCoolBarVisible()){
				toolbarWasInvisible = true;
			}else{
				toolbarWasInvisible = false;
				toggleToolbarAction.run();
			}
			shell.setMenuBar(null);		
			inFullScreenMode = true;
			setText(EXIT_COMPACT_MODE);
			setImageDescriptor(exitFullScreenImage);
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	public void init(IWorkbenchWindow window) {		
	}

}




