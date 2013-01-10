package org.csstudio.opibuilder.util;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.widgetActions.OpenFileAction;
import org.csstudio.rap.core.DisplayManager;
import org.csstudio.rap.core.preferences.ServerScope;
import org.csstudio.webopi.WebOPIConstants;
import org.csstudio.webopi.util.RequestUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	@Override
	protected GC iGetImageGC(Image image) {
		return null;
	}

	@Override
	protected void iOpenFileActionRun(OpenFileAction openFileAction) {

	}

	@Override
	protected void iAddPaintListener(Control control,
			PaintListener paintListener) {
	}

	@Override
	protected void iRemovePaintListener(Control control,
			PaintListener paintListener) {
	}

	@Override
	protected void iRegisterRCPRuntimeActions(
			ActionRegistry actionRegistry, IOPIRuntime opiRuntime) {
	}

	@Override
	protected void iappendRCPRuntimeActionsToMenu(
			ActionRegistry actionRegistry, IMenuManager menu) {
	}

	@Override
	protected void iRapActivatebaseEditPart(AbstractBaseEditPart editPart) {
		DisplayManager.getInstance().registerObject(editPart);
		try {			
			DisplayManager.getInstance().addDisplayDisposeListener(
					editPart.getRoot().getViewer().getControl().getDisplay(),
					editPart.getDisplayDisposeListener());
		} catch (Exception e) {
			ErrorHandlerUtil.handleError("Failed to add dispose listener.", e);
		}
	}

	@Override
	protected void iRapDeactivatebaseEditPart(AbstractBaseEditPart editPart) {
		try {

			DisplayManager.getInstance().removeDisplayDisposeListener(
					editPart.getRoot().getViewer().getControl().getDisplay(),
					editPart.getDisplayDisposeListener());
		} catch (Exception e) {
			ErrorHandlerUtil.handleError(
					"Failed to remove dispose listener.", e);
		}
		DisplayManager.getInstance().unRegisterObject(editPart);
	}

	@Override
	protected void iRapOpenOPIInNewWindow(IPath path) {
		HttpServletRequest request = RWT.getRequest();
    	String url = request.getRequestURL().toString();
    	//to allow multilple browser instances, session id is not allowed
    	if(url.contains(";jsessionid")) //$NON-NLS-1$
    		url = url.substring(0, url.indexOf(";jsessionid"));//$NON-NLS-1$			    	
    	ExternalBrowser.open("_blank", url+"?opi=" + path.toString(), SWT.None);
    	
	}

	@Override
	protected void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable) {
		try {
			DisplayManager.getInstance().addDisplayDisposeListener(display, runnable);
		} catch (Exception e) {			
		}
	}

	@Override
	protected void iRapPlayWavFile(IPath absolutePath) {
		if(!ResourceUtil.isURL(absolutePath.toString())){
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Not support", 
					"The sound file path must be an URL!");
			return;		}
			
		String code = "document.getElementById(\"dummy\").innerHTML=\"<embed src=\\\""+ //$NON-NLS-1$
				absolutePath + "\\\" hidden=\\\"true\\\" autostart=\\\"true\\\" loop=\\\"false\\\" />\""; //$NON-NLS-1$
		JSExecutor.executeJS(code);
	}

	@Override
	protected void iRapOPIViewCreatePartControl(OPIView opiView, Composite parent) {
		if(opiView.getOPIInput() == null && OPIView.isOpenFromPerspective()){
			OPIView.setOpenFromPerspective(false);
			RunnerInput runnerInput = RequestUtil.getOPIPathFromRequest();
			IPath opiPath = null;
			if(runnerInput == null){				
				String s = RWT.getRequest().getServletPath();
				if(s.equals(WebOPIConstants.MOBILE_S_SERVELET_NAME)) //$NON-NLS-1$
					opiPath = PreferencesHelper.getMobileStartupOPI();
				else
					opiPath = PreferencesHelper.getStartupOPI();
				if(opiPath == null)
					throw new RuntimeException(
							"OPI file path or OPI Repository is not specified in URL or preferences.");
			}
			try {
				opiView.setOPIInput(runnerInput == null ? new RunnerInput(
						opiPath, null, null) : runnerInput);
			} catch (PartInitException e) {
				throw new RuntimeException(e);
			}
		}
		opiView.getOPIRuntimeDelegate().createGUI(parent);
		if(!RequestUtil.isSimpleMode())
			opiView.createToolbarButtons();
	}

	@Override
	protected void iRapPluginStartUp() {
		Platform.getPreferencesService().setDefaultLookupOrder(
				OPIBuilderPlugin.PLUGIN_ID, null, new String[] { //
				InstanceScope.SCOPE, //
				ConfigurationScope.SCOPE, //
				ServerScope.SCOPE, //$NON-NLS-1$
				DefaultScope.SCOPE});		
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

}
