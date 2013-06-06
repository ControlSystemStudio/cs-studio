package org.csstudio.alarm.beast.ui;

import java.util.logging.Level;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Configures the initial size and appearance of a workbench window.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		Shell shell = configurer.getWindow().getShell();
		shell.setText("Web Alarm");
		shell.setMaximized(true);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowMenuBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowCoolBar(true);
		configurer.setTitle("Web Alarm");

		configurer.setShellStyle(SWT.NO_TRIM);

		String treeRoot = null;
		try {
			// Forec reload to handle new alarm tree
			AlarmClientModel.getInstance().readConfig(null);
			
			// Force reload default tree root
			treeRoot = Preferences.getAlarmTreeRoot();
			AlarmClientModel.getInstance().setConfigurationName(treeRoot, null);
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE,
					"Unable to reload alarm configuration : " + treeRoot, e);
		}
	}

}
