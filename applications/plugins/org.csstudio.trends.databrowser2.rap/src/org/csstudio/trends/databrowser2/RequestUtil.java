package org.csstudio.trends.databrowser2;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.trends.databrowser2.persistence.URLPath;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
			if (ResourceUtil.isURL(pltPath))
				path = new URLPath(pltPath);
			else {
				path = new Path(pltPath);
				if (!path.isAbsolute()) {
					IPath repoPath = Preferences.getPltRepository();
					if (repoPath == null)
						path = null;
					else
						path = repoPath.append(path);
				}
			}
			return path;
		}
		return null;
	}

}
