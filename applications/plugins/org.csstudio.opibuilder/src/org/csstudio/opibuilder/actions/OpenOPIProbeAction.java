package org.csstudio.opibuilder.actions;

import java.io.IOException;
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
import org.eclipse.jface.dialogs.MessageDialog;

/**Action to open probe OPI.
 * @author Xihui Chen
 *
 */
public class OpenOPIProbeAction extends ProcessVariablePopupAction {

	private static final String MACRO_NAME = "probe_pv"; //$NON-NLS-1$

	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		
		IPath probeOPIPath = PreferencesHelper.getProbeOPIPath();
		
		if(probeOPIPath == null || probeOPIPath.isEmpty()){
			URL url = FileLocator.find(OPIBuilderPlugin.getDefault().getBundle(),
					new Path("opi/probe.opi"), null); //$NON-NLS-1$
			try {
				url = FileLocator.toFileURL(url);
			} catch (IOException e) {
				MessageDialog.openError(null, "No Probe OPI Defined", 
						"There is no probe OPI defined yet, please define your probe OPI from BOY preference page");
			}
			probeOPIPath = new URLPath(url.getPath());
		}
		
		LinkedHashMap<String, String> macros = new LinkedHashMap<String, String>();
		if(pv_names.length ==1){
			macros.put(MACRO_NAME, pv_names[0].getName());
		}else{
			int i=0;
			for(IProcessVariable pv : pv_names){
				macros.put(MACRO_NAME + "_" + Integer.toString(i), pv.getName()); //$NON-NLS-1$
			}
		}
		
		MacrosInput macrosInput = new MacrosInput(macros, true);
		
		RunModeService.getInstance().runOPI(probeOPIPath, 
				TargetWindow.SAME_WINDOW, null, macrosInput);
	}

}
