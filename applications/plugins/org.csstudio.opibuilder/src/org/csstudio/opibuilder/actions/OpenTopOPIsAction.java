/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

/**Open top OPIs in run mode.
 * @author Xihui Chen
 */
public class OpenTopOPIsAction implements IWorkbenchWindowPulldownDelegate {

	private Menu opiListMenu;
	private static Image OPI_RUNTIME_IMAGE = CustomMediaFactory.getInstance().getImageFromPlugin(
			OPIBuilderPlugin.PLUGIN_ID, "icons/OPIRunner.png"); //$NON-NLS-1$
	public Menu getMenu(Control parent) {
		dispose();
		final Map<IPath, MacrosInput> topOPIs = loadTopOPIs();
		if(topOPIs == null)
			return null;
		opiListMenu = new Menu(parent);
			for(final IPath path : topOPIs.keySet()){
				if(path != null){
					MenuItem item = new MenuItem(opiListMenu, SWT.PUSH);
					item.setText(path.lastSegment());
					item.setImage(OPI_RUNTIME_IMAGE);
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							RunModeService.getInstance().runOPI(
									path, TargetWindow.SAME_WINDOW, null, topOPIs.get(path));
						}
					});
				}
			}
		return opiListMenu;
	}

	/**
	 * @return the top OPIs from preference settings
	 */
	private Map<IPath, MacrosInput> loadTopOPIs() {
		final Map<IPath, MacrosInput> topOPIs;
		try {
			topOPIs = PreferencesHelper.getTopOPIs();
			if(topOPIs == null || topOPIs.keySet().size() == 0){
				MessageDialog.openWarning(null, "Warning", "No top OPIs were set in preference settings.");
				return null;
			}

		} catch (Exception e) {
			String message = NLS.bind("Failed to load top OPIs from preference settings. \n {0}", e);
			MessageDialog.openError(null, "Error in loading top OPIs", message);
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
			return null;
		}
		return topOPIs;
	}

	public void dispose() {
		if(opiListMenu != null && !opiListMenu.isDisposed()){
			for(MenuItem m : opiListMenu.getItems())
				m.dispose();
			opiListMenu.dispose();
			opiListMenu = null;
		}


	}

	public void init(IWorkbenchWindow window) {
        // NOP
	}

	public void run(IAction action) {
		Map<IPath, MacrosInput> topOPIs = loadTopOPIs();
		if(topOPIs != null && topOPIs.keySet().size() >= 1){
			IPath path = (IPath) topOPIs.keySet().toArray()[0];
			if(path != null){
				RunModeService.getInstance().runOPI(
						path, TargetWindow.SAME_WINDOW, null, topOPIs.get(path));
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	    // NOP
	}
}
