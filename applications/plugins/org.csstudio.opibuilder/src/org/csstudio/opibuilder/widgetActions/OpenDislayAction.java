package org.csstudio.opibuilder.widgetActions;

import java.io.FileNotFoundException;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class OpenDislayAction extends AbstractWidgetAction {
	
	
	
	private IPath path;
	public static final String PROP_PATH = "path";//$NON-NLS-1$
	public static final String PROP_MACROS = "macros";//$NON-NLS-1$
	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""), new String[]{"opi"}));
		addProperty(new StringProperty(PROP_MACROS, "Macros", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public void run() {
		//read file
		IFile[] files = 
			ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
					ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path));
		
		if(files.length < 1)
			try {
				throw new FileNotFoundException("The file " + path.toString() + "does not exist!");
			} catch (FileNotFoundException e) {
				CentralLogger.getInstance().error(this, e);
			}
		RunModeService.getInstance().runOPI(files[0], new DisplayModel());
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
