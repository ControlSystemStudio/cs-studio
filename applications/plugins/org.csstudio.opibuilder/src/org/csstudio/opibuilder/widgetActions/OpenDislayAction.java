package org.csstudio.opibuilder.widgetActions;

import java.io.FileNotFoundException;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class OpenDislayAction extends AbstractWidgetAction {
	
	
	
	public static final String PROP_PATH = "path";//$NON-NLS-1$
	public static final String PROP_MACROS = "macros";//$NON-NLS-1$
	public static final String PROP_REPLACE = "replace";//$NON-NLS-1$
	private boolean ctrlPressed = false;
	private boolean shiftPressed = false;
	
	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""), new String[]{"opi"}));
		addProperty(new StringProperty(PROP_MACROS, "Macros", WidgetPropertyCategory.Basic, ""));
		addProperty(new BooleanProperty(PROP_REPLACE, "Replace", WidgetPropertyCategory.Basic, true));
	}

	@Override
	public void run() {
		//read file
		IFile[] files = 
			ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
					ResourcesPlugin.getWorkspace().getRoot().getLocation().append(getPath()));
		
		if(files.length < 1)
			try {
				throw new FileNotFoundException("The file " + getPath().toString() + "does not exist!");
			} catch (FileNotFoundException e) {
				CentralLogger.getInstance().error(this, e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
						e.getMessage());
			
			}
		else {
			if(!ctrlPressed && !shiftPressed && isReplace()){
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().closeEditor(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().
							getActivePage().getActiveEditor(), false);
			}
			TargetWindow target;
			if (shiftPressed && !ctrlPressed)
				target = TargetWindow.NEW_WINDOW;
			else
				target = TargetWindow.SAME_WINDOW;
			
			RunModeService.getInstance().runOPI(files[0], target);
			
		}
	}
	
	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}

	
	/**
	 * @param ctrlPressed the ctrlPressed to set
	 */
	public final void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	/**
	 * @param shiftPressed the shiftPressed to set
	 */
	public final void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	private boolean isReplace(){
		return (Boolean)getPropertyValue(PROP_REPLACE);
	}
	
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {
				
				public Object getParent(Object o) {
					return null;
				}
				
				public String getLabel(Object o) {
					return getActionType().getDescription();
				}
				
				public ImageDescriptor getImageDescriptor(Object object) {
					return getActionType().getIconImage();
				}
				
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		
		return null;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_DISPLAY;
	}


}
