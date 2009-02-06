package org.csstudio.sns.startuphelper;

import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.security.UiLoginCallbackHandler;

/**Help to authenticate in the startup code <code>Application.java</code>. 
 * The codes in this class will cause the loading process of org.csstudui.platfrom, 
 * which may force the workspace to be set. So the codes below cannot be put
 * into <code>Application.java</code> directly. 
 * 
 * @author Xihui Chen
 *
 */
public class StartupAuthenticationHelper {

	public static void authenticate(final String username, final String password){
    	final SecurityFacade sf = SecurityFacade.getInstance();
		sf.setLoginCallbackHandler(new UiLoginCallbackHandler(Messages.StartupAuthenticationHelper_Login, 
				Messages.StartupAuthenticationHelper_LoginTip, username, password));		
		sf.authenticateApplicationUser();	
	}
	
	
}
