package org.csstudio.opibuilder.util;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.widgetActions.OpenFileAction;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public abstract class SingleSourceHelper {

	private static final SingleSourceHelper IMPL;
	
	static {
		IMPL = (SingleSourceHelper)ImplementationLoader.newInstance(
				SingleSourceHelper.class);
	}
	
	public static GC getImageGC(final Image image){
		if(IMPL == null)
			return null;
		return IMPL.iGetImageGC(image);
	}
	
	protected abstract GC iGetImageGC(final Image image);
	
	
	public static void openFileActionRun(OpenFileAction openFileAction){
		if(IMPL != null)			
			IMPL.iOpenFileActionRun(openFileAction);
		else {
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Not Implemented", 
					"Sorry, open File action is not implemented for WebOPI!");
		}
	}
	
	protected abstract void iOpenFileActionRun(OpenFileAction openFileAction);	

	public static void addPaintListener(final Control control, PaintListener paintListener){
		if(IMPL != null)
			IMPL.iAddPaintListener(control, paintListener);
	}

	protected abstract void iAddPaintListener(Control control,
			PaintListener paintListener);
	
	public static void removePaintListener(final Control control, PaintListener paintListener){
		if(IMPL != null)
			IMPL.iRemovePaintListener(control, paintListener);
	}

	protected abstract void iRemovePaintListener(Control control,
			PaintListener paintListener);
	
	public static void registerRCPRuntimeActions(
			ActionRegistry actionRegistry, IOPIRuntime opiRuntime){
		if(IMPL != null)
			IMPL.iRegisterRCPRuntimeActions(actionRegistry, opiRuntime);
	}

	protected abstract void iRegisterRCPRuntimeActions(
			ActionRegistry actionRegistry, IOPIRuntime opiRuntime);
	
	public static void appendRCPRuntimeActionsToMenu(
			ActionRegistry actionRegistry, IMenuManager menu){
		if(IMPL != null)
			IMPL.iappendRCPRuntimeActionsToMenu(actionRegistry, menu);
	}

	protected abstract void iappendRCPRuntimeActionsToMenu(
			ActionRegistry actionRegistry, IMenuManager menu);
	
	
	public static IPath rcpGetPathFromWorkspaceFileDialog(
			IPath startPath, String[] extensions){
		if(IMPL != null)
			return IMPL.iRcpGetPathFromWorkspaceFileDialog(startPath, extensions);
		return null;
	}
	
	protected abstract IPath iRcpGetPathFromWorkspaceFileDialog(
			IPath startPath, String[] extensions);
	
	public static void rapActivateBaseEditPart(AbstractBaseEditPart editPart){
		if(IMPL != null)
			IMPL.iRapActivatebaseEditPart(editPart);
	}

	protected abstract void iRapActivatebaseEditPart(AbstractBaseEditPart editPart);
	
	public static void rapDeactivateBaseEditPart(AbstractBaseEditPart editPart){
		if(IMPL != null)
			IMPL.iRapDeactivatebaseEditPart(editPart);
	}

	protected abstract void iRapDeactivatebaseEditPart(AbstractBaseEditPart editPart);
	
	public static void rapOpenOPIInNewWindow(IPath path){
		if(IMPL != null)
			IMPL.iRapOpenOPIInNewWindow(path);
	}

	protected abstract void iRapOpenOPIInNewWindow(IPath path);
	
	public static void rapAddDisplayDisposeListener(Display display, Runnable runnable){
		if(IMPL != null)
			IMPL.iRapAddDisplayDisposeListener(display, runnable);
	}

	protected abstract void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable);

	public static void rapPlayWavFile(IPath absolutePath) {
		if(IMPL != null)
			IMPL.iRapPlayWavFile(absolutePath);
	}

	protected abstract void iRapPlayWavFile(IPath absolutePath);

	public static void rapOPIViewCreatePartControl(OPIView opiView, Composite parent) {
		if(IMPL != null)
			IMPL.iRapOPIViewCreatePartControl(opiView, parent);
	}

	protected abstract void iRapOPIViewCreatePartControl(OPIView opiView, Composite parent);
	
	/**
	 * Plugin Startup code for WebOPI.
	 */
	public static void rapPluginStartUp(){
		if(IMPL != null)
			IMPL.iRapPluginStartUp();
	}

	protected abstract void iRapPluginStartUp();

	public static void rapOpenWebPage(String hyperLink) {
		if(IMPL != null)
			IMPL.iRapOpenWebPage(hyperLink);		
	}

	protected abstract void iRapOpenWebPage(String hyperLink);
}
