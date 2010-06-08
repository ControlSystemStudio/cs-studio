package org.csstudio.platform.ui.util;

import org.csstudio.platform.ui.display.IOpenDisplayAction;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**Utility class for display operation.
 * @author Xihui Chen
 *
 */
public class DisplayUtil {

	/**Open display with corresponding runtime.
	 * @param path the path of display file.
	 * @param data the input data. set as null if it is not needed.
	 * @throws Exception
	 */
	public static void openDisplay(String path, String data) throws Exception{
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		IConfigurationElement[] confElements = 
			extReg.getConfigurationElementsFor("org.csstudio.platform.ui.openDisplay"); //$NON-NLS-1$
		String ext = path.substring(path.lastIndexOf('.') +1);
		if(ext == null || ext.trim().length() == 0)
			throw new Exception("Display file extension can not be empty!");
		for(IConfigurationElement element : confElements){
			String fileExt = element.getAttribute("file_extension"); //$NON-NLS-1$
			if(fileExt.equals(ext)){
				IOpenDisplayAction action = 
					(IOpenDisplayAction) element.createExecutableExtension("class"); //$NON-NLS-1$
				action.openDisplay(path, data);
				return;
			}
		}
		throw new Exception(NLS.bind(
				"No extension of openDisplay for file type .{0} implemented.", ext));
	}
	
}
