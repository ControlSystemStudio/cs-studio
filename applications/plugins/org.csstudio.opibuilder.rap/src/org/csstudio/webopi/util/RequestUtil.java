package org.csstudio.webopi.util;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
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
		 String s = request.getServletPath();
		if(s.equals(WebOPIConstants.MOBILE_S_SERVELET_NAME) 
				|| s.equals(WebOPIConstants.STANDALONE_SERVELET_NAME))
			return true;
		 return false;
	}
	
	/**
	 * @return the opi path specified in URL. null if no opi parameter is specified.
	 */
	public static RunnerInput getOPIPathFromRequest(){
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
			String[] inputs = null;
			try {
				inputs = StringSplitter.splitIgnoreInQuotes(opiPath, '|', true);
				opiPath = inputs[0];
			} catch (Exception e) {
				e.printStackTrace();
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
			MacrosInput macrosInput = null;
			if(inputs != null && inputs.length >1){
				int i=0;
				for(String m : inputs){
					if(i++ ==0)
						continue;
					try {
						String[] macro = StringSplitter.splitIgnoreInQuotes(m, '=', true);
						if(macro.length == 2){
							if(macrosInput == null)
								macrosInput= new MacrosInput(
										new LinkedHashMap<String, String>(), true);
							macrosInput.put(macro[0], macro[1]);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			}
			return new RunnerInput(path, null, macrosInput);
		}
		
		return null;
	}

}
