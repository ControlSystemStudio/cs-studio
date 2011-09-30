package org.csstudio.webopi.util;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.webopi.WebOPIConstants;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;

/**
 * Utility for request related functions.
 * @author Xihui Chen
 *
 */
public class RequestUtil {
	

	public static boolean isSimpleMode(){
    	HttpServletRequest request = RWT.getRequest();
		String mode = request.getParameter("startup"); //$NON-NLS-1$
		 if(mode!=null && mode.equals(WebOPIConstants.SIMPLE_ENTRY_POINT)) //$NON-NLS-1$
			 return true;	
		 return false;
	}
	
	/**
	 * @return the opi path specified in URL. null if no opi parameter is specified.
	 */
	public static IPath getOPIPathFromRequest(){
		HttpServletRequest request = RWT.getRequest();
		 String opiPath = request.getParameter(WebOPIConstants.OPI_PARAMETER ); //$NON-NLS-1$
		
		IPath path = null;
		if(opiPath != null && !opiPath.isEmpty()){
			try {
				String newPath = MacroUtil.replaceMacros(opiPath,
						new IMacroTableProvider() {
							public String getMacroValue(String macroName) {
								return PreferencesHelper.getMacros().get(
										macroName);

							}
						});
				opiPath = newPath;
			} catch (InfiniteLoopException e) {
				ErrorHandlerUtil.handleError("Failed to replace macros", e);
			}
			if(ResourceUtil.isURL(opiPath))
				path = new URLPath(opiPath);
			else{
				path = new Path(opiPath);
				if(!path.isAbsolute()){
					IPath repoPath = PreferencesHelper.getOPIRepository();
					if(repoPath == null)
						path = null;
					else
						path=repoPath.append(path);
				}
			}
		}else {
			return null;
		}
		return path;
	}

}
