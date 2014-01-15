package org.csstudio.webopi;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.webopi.util.RequestUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Configures the initial size and appearance of a workbench window.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {


	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    
	@Override
    public void postWindowCreate() {
    	super.postWindowCreate();
    	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    	Shell shell = configurer.getWindow().getShell();
    	shell.setText("WebOPI");
    	shell.setMaximized(true);
    	RunnerInput runnerInput = RequestUtil.getOPIPathFromRequest();
		
		if(runnerInput == null){
			IPath path = null;
			String s = RWT.getRequest().getServletPath();
			if(s.contains(WebOPIConstants.MOBILE_SERVELET_NAME)) //$NON-NLS-1$
				path = PreferencesHelper.getMobileStartupOPI();
			else
				path = PreferencesHelper.getStartupOPI();
			if(path == null)
				return;
			if (path.getFileExtension().toLowerCase().equals("opi")) {
				RunModeService.getInstance().runOPI(path, TargetWindow.SAME_WINDOW, null);
			} else {
				runOther(path);
			}
		} else {
			IPath path = runnerInput.getPath();
			if (path != null) {
				if (path.getFileExtension().toLowerCase().equals("opi")) {
					RunModeService.getInstance().runOPI(path, TargetWindow.SAME_WINDOW, null, runnerInput.getMacrosInput());
				} else {
					runOther(path);
				}
			}
		}
	}

	private void runOther(final IPath path) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			SingleSourceHelper.openEditor(page, path);
		} catch (Exception e) {
			String message = NLS.bind("Failed to open the editor. \n {0}", e);
			MessageDialog.openError(null, "Error in opening OPI", message);
		}
	}
    
    public void preWindowOpen() {
    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();    
       
//        configurer.setShowCoolBar(!RequestUtil.isStandaloneMode());
        configurer.setShowMenuBar(false);
        configurer.setShowStatusLine(false);        
        configurer.setTitle("WebOPI");

        configurer.setShellStyle(SWT.NO_TRIM);
    }
    
   
    
}
