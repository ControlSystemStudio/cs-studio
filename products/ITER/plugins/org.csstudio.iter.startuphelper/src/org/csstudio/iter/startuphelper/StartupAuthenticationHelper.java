package org.csstudio.iter.startuphelper;

import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.UiLoginCallbackHandler;

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
		Credentials defaultCredentials;
		if(username == null)
			defaultCredentials = Credentials.ANONYMOUS;
		else
			defaultCredentials = new Credentials(username, password);
    	final SecurityFacade sf = SecurityFacade.getInstance();
		sf.setLoginCallbackHandler(new UiLoginCallbackHandler(Messages.StartupAuthenticationHelper_Login, 
				Messages.StartupAuthenticationHelper_LoginTip, defaultCredentials));		
		sf.authenticateApplicationUser();	
	}
	
	
}
