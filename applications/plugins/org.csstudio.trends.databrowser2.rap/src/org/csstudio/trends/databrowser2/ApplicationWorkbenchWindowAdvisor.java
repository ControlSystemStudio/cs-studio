package org.csstudio.trends.databrowser2;

import java.util.logging.Level;

import org.csstudio.trends.databrowser2.editor.DataBrowserEditor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
		shell.setText("DataBrowser");
		shell.setMaximized(true);
		IPath path = RequestUtil.getPltPathFromRequest();

		if (path == null) {
			return;
		}
		IWorkbenchWindow targetWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		DataBrowserInput dataBrowserInput = new DataBrowserInput(path);
		try {
			targetWindow.getActivePage().openEditor(dataBrowserInput,
					DataBrowserEditor.ID);
		} catch (PartInitException e) {
			Activator.getLogger().log(Level.WARNING, "Cannot open editor", e); //$NON-NLS-1$
		}
	}

	public void preWindowOpen() {

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		// configurer.setShowCoolBar(!RequestUtil.isStandaloneMode());
		configurer.setShowMenuBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("DataBrowser");

		configurer.setShellStyle(SWT.NO_TRIM);
	}

}
