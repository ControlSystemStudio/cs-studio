package org.csstudio.utility.casnooper;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */ 

import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.utility.casnooper.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCallbackHandler1 implements ILoginCallbackHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoginCallbackHandler1.class);
    
	@Override
    public Credentials getCredentials() {
		
//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String xmppUserName = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"xmppUserName", false);
//		String xmppPassword = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences", 
//				"xmppPassword", false);

	    IPreferencesService prefs = Platform.getPreferencesService();
	    String xmppUserName = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.XMPP_USER_NAME, "", null);
	    String xmppPassword = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.XMPP_PASSWORD, "", null);  
		
		return new Credentials(xmppUserName, xmppPassword);
	}

	@Override
    public void signalFailedLoginAttempt() {
		LOG.error("XMPP login failed");
	}

}
