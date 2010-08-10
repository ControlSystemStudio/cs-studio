package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;

/**The utility class to facilitate Javascript programming.
 * @author Xihui Chen
 *
 */
public class ScriptUtil {
	
	/**Open an OPI.
	 * @param widgetController the widgetController to which the script is attached. 
	 * @param relative_path the path of the OPI relative to the Display file of the widgetContoller. 
	 * @param newWindow true if it will be opened in a new window. false if in a new tab.
	 * @param macrosInput the macrosInput. null if no macros needed.
	 */
	public final static void openOPI(AbstractBaseEditPart widgetController, 
			String relative_path, boolean newWindow, MacrosInput macrosInput){
		IPath  path = ResourceUtil.buildAbsolutePath(
				widgetController.getWidgetModel(), ResourceUtil.getPathFromString(relative_path));
		RunModeService.getInstance().runOPI(path, 
				newWindow ? TargetWindow.NEW_WINDOW : TargetWindow.SAME_WINDOW, null, macrosInput);
	}
			
	
	
}
