package org.csstudio.opibuilder.widgetActions;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
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
		addProperty(new MacrosProperty(PROP_MACROS, "Macros", WidgetPropertyCategory.Basic, 
				new MacrosInput(new HashMap<String, String>(), true)));
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
				IEditorPart activeEditor = 
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().getActiveEditor();
				if(activeEditor instanceof OPIRunner){
					DisplayOpenManager manager = 
						(DisplayOpenManager)(activeEditor.getAdapter(DisplayOpenManager.class));
					manager.openNewDisplay();
					try {
						RunModeService.getInstance().replaceActiveEditorContent(new RunnerInput(
								files[0], manager, getMacrosInput()));
					} catch (PartInitException e) {
						CentralLogger.getInstance().error(this, "Failed to open " + files[0], e);
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Open file error", 
								"Failed to open " + files[0]);
					}
				}
			}else{
				TargetWindow target;
				if (shiftPressed && !ctrlPressed)
					target = TargetWindow.NEW_WINDOW;
				else
					target = TargetWindow.SAME_WINDOW;
			
				RunModeService.getInstance().runOPI(files[0], target, null, getMacrosInput(), null);
			}
		}
	}
	
	private IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}

	private MacrosInput getMacrosInput(){
		MacrosInput macrosInput = ((MacrosInput)getPropertyValue(PROP_MACROS)).getCopy();
		IEditorPart activeEditor = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().getActiveEditor();
		if(macrosInput.isInclude_parent_macros() && activeEditor instanceof OPIRunner){
			Map<String, String> macrosMap = ((OPIRunner)activeEditor).getDisplayModel().getMacroMap();
			macrosInput.getMacrosMap().putAll(macrosMap);			
		}
		macrosInput.setInclude_parent_macros(false);
		return macrosInput;
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
	
	
	

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_DISPLAY;
	}


	@Override
	public String getDescription() {
		return getActionType().getDescription() + " " + getPath();
	}
	
}
