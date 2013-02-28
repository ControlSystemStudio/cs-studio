package org.csstudio.trends.databrowser2;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.rwt.RWT;

/**
 * Utility for request related functions.
 * 
 * @author Davy Dequidt
 * 
 */
public class RequestUtil {

	/**
	 * @return the plt path specified in URL. null if no plt parameter is
	 *         specified.
	 */
	public static IPath getPltPathFromRequest() {
		HttpServletRequest request = RWT.getRequest();
		String pltPath = request
				.getParameter(WebDataBrowserConstants.PLT_PARAMETER); //$NON-NLS-1$
		IPath path = null;
		if (pltPath != null && !pltPath.isEmpty()) {
		    path = SingleSourcePlugin.getResourceHelper().newPath(pltPath);
			if (!path.isAbsolute()) {
				IPath repoPath = Preferences.getPltRepository();
				if (repoPath == null)
					path = null;
				else
					path = repoPath.append(path);
			}
			return path;
		}
		return null;
	}

}
