package org.csstudio.webopi;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.eclipse.core.runtime.Path;
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
    	shell.setText("hello Rap");
    	shell.setMaximized(true);
    	
    	RunModeService.getInstance().runOPI(new Path(
    			"C:\\Users\\5hz\\Work\\java test\\runtime-SNS_CSS.product\\BOY Examples\\1_1_Start_Up.opi"), 
    			TargetWindow.SAME_WINDOW, null);
    	
    }
    
    public void preWindowOpen() {
    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();    
       
        configurer.setShowCoolBar(true);
        configurer.setShowMenuBar(false);
        configurer.setShowStatusLine(false);
        
        configurer.setTitle("Hello RAP");

        configurer.setShellStyle(SWT.NO_TRIM);
    }
    
   
    
}
