package org.csstudio.opibuilder.util;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;

public class RequestUtil {
	
	public static boolean isStandaloneMode(){
    	HttpServletRequest request = RWT.getRequest();
		String mode = request.getParameter( "mode" );
		 if(mode!=null && mode.equals("standalone"))
			 return true;
		 mode=request.getParameter("startup");
		 if(mode!=null && mode.equals("webopi_s"))
			 return true;	
		 return false;
	}
	
	public static IPath getOPIPathFromRequest(){
		HttpServletRequest request = RWT.getRequest();
		 String opiPath = request.getParameter( "opi" );
		IPath path = null;
		if(opiPath != null && !opiPath.isEmpty()){
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
			path = PreferencesHelper.getStartupOPI();
		}
		return path;
	}

}
