/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.net.URL;
import java.util.LinkedHashMap;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

/**Action to open probe OPI.
 * @author Xihui Chen
 * @author Kay Kasemir
 */
public class OpenOPIProbeAction extends ProcessVariablePopupAction {

	private static final String MACRO_NAME = "probe_pv"; //$NON-NLS-1$
    private Shell shell = null;

    /** Track shell */
    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart part)
    {
        if (part != null)
            shell  = part.getSite().getShell();
        else
            shell = null;
    }

    /** {@inheritDoc} */
	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		IPath probeOPIPath = PreferencesHelper.getProbeOPIPath();

		// When not defined, try built-in probe opi example
		if(probeOPIPath == null || probeOPIPath.isEmpty()){
			URL url = FileLocator.find(OPIBuilderPlugin.getDefault().getBundle(),
					new Path("opi/probe.opi"), null); //$NON-NLS-1$
			try {
				url = FileLocator.toFileURL(url);
			} catch (Throwable e) {
				MessageDialog.openError(shell, "No Probe OPI",
						"Cannot open probe OPI.\nPlease define your probe OPI on BOY preference page.");
			}
			probeOPIPath = new URLPath(url.getPath());
		}

		LinkedHashMap<String, String> macros = new LinkedHashMap<String, String>();
		if(pv_names.length >0)
			macros.put(MACRO_NAME, pv_names[0].getName());

		int i=0;
		for(IProcessVariable pv : pv_names){
			macros.put(MACRO_NAME + "_" + Integer.toString(i), pv.getName()); //$NON-NLS-1$
			i++;
		}

		MacrosInput macrosInput = new MacrosInput(macros, true);

		// Errors in here will show in dialog and error log
		RunModeService.getInstance().runOPI(probeOPIPath,
				TargetWindow.SAME_WINDOW, null, macrosInput);
	}
}
