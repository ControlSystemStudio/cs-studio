package org.csstudio.webopi;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.webopi.util.RequestUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
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
		IPath path = RequestUtil.getOPIPathFromRequest();
		String s = RWT.getRequest().getServletPath();
		if(path == null){
			if(s.equals(WebOPIConstants.MOBILE_SERVELET_NAME)) //$NON-NLS-1$
				path = PreferencesHelper.getMobileStartupOPI();
			else
				path = PreferencesHelper.getStartupOPI();
		}
		if(path == null)
			return;
//		 if(!RequestUtil.isStandaloneMode())			 		
			RunModeService.getInstance().runOPI(path, 
    			TargetWindow.SAME_WINDOW, null);
    	
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
