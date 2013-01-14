package org.csstudio.trends.databrowser2.util;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.rap.core.DisplayManager;
import org.csstudio.rap.core.preferences.ServerScope;
import org.csstudio.trends.databrowser2.Activator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	@Override
	protected GC iGetImageGC(Image image) {
		return null;
	}

	// @Override
	// protected void iOpenFileActionRun(OpenFileAction openFileAction) {
	//
	// }

	@Override
	protected void iAddPaintListener(Control control,
			PaintListener paintListener) {
	}

	@Override
	protected void iRemovePaintListener(Control control,
			PaintListener paintListener) {
	}

	@Override
	protected void iRapOpenPltInNewWindow(IPath path) {
		HttpServletRequest request = RWT.getRequest();
    	String url = request.getRequestURL().toString();
    	//to allow multilple browser instances, session id is not allowed
    	if(url.contains(";jsessionid")) //$NON-NLS-1$
    		url = url.substring(0, url.indexOf(";jsessionid"));//$NON-NLS-1$			    	
    	ExternalBrowser.open("_blank", url+"?plt=" + path.toString(), SWT.None);
    	
	}

	@Override
	protected void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable) {
		try {
			DisplayManager.getInstance().addDisplayDisposeListener(display,
					runnable);
		} catch (Exception e) {
		}
	}

	@Override
	protected void iRapPluginStartUp() {
		Platform.getPreferencesService().setDefaultLookupOrder(
				Activator.PLUGIN_ID, null, new String[] { //
				InstanceScope.SCOPE, //
						ConfigurationScope.SCOPE, //
						ServerScope.SCOPE, //$NON-NLS-1$
						DefaultScope.SCOPE });
	}

	@Override
	protected IPath iRcpGetPathFromWorkspaceFileDialog(IPath startPath,
			String[] extensions) {
		return null;
	}

	@Override
	protected void iRapOpenWebPage(String hyperLink) {
		ExternalBrowser.open("_blank", hyperLink, SWT.NONE);
	}

	protected String iOpenFileBrowser(Shell shell, int style) {
		throw new RuntimeException("Not yet implemented for web version."); 
	}
	
}
