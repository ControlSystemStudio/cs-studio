
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.remotetool;

import org.csstudio.ams.remotetool.internal.PreferenceKeys;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 * @version 2.0, 2011-07-06
 */
public class AmsRemoteTool implements IApplication, IGenericServiceListener<ISessionService> {
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(AmsRemoteTool.class);
    
    /** Command line helper */
    private CommandLine cl;
    
    private ISessionService xmppSession;
    
    public AmsRemoteTool() {
        cl = null;
        xmppSession = null;
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) throws Exception {
        
        int iResult = ApplicResult.RESULT_ERROR_GENERAL.getApplicResultNumber();
        
        String[] args = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        cl = new CommandLine(args);
        
        LOG.info("AmsRemoteTool started...");
        
        if(cl.exists("help") || cl.exists("?")) {
            usage();
            return iResult;
        }
        
        // Check the command line arguments
        // We expect:
        // -applicname - Name of the application to stop
        // -host - Name of the computer on which the application runs
        // -username - Name of the user
        // -pw - Password for stopping
        if(!cl.exists("host") 
                || !cl.exists("applicname") 
                || !cl.exists("username") 
                || !cl.exists("pw"))  {
            
            LOG.error("One or more application arguments are missing.");
            usage();
            return iResult;
        }
        
        Activator.getDefault().addSessionServiceListener(this);

        String applicName = (cl.value("applicname") != null) ? cl.value("applicname") : "";
        String host = (cl.value("host") != null) ? cl.value("host") : "";
        String user = (cl.value("username") != null) ? cl.value("username") : "";
        String pw = (cl.value("pw") != null) ? cl.value("pw") : "";
                
        LOG.info("Try to stop " + applicName + " on host " + host + ". Running under the account: " + user);

        synchronized (this) {
            try {
                this.wait(2000);
            } catch(InterruptedException ie) {
                LOG.error("*** InterruptedException ***: " + ie.getMessage());
            } 
        }
        
        if (xmppSession == null) {
            return ApplicResult.RESULT_ERROR_XMPP.getApplicResultNumber();
        }
        
        ApplicationStopper appStopper = new ApplicationStopper(xmppSession);
        iResult = appStopper.stopApplication(host, applicName, user, pw);
        
        if (xmppSession != null) {
            xmppSession.disconnect();
        }
        
        return iResult;
    }
    
    public void usage() {
        LOG.info("AmsRemoteTool, Markus Moeller, MKS 2, (C)2011");
        LOG.info("This application stops an AMS process via XMPP action call.");
        LOG.info("Options:");
        LOG.info("-host - Name of the computer on which the AMS application is running.");
        LOG.info("-applicname - XMPP account name of the AMS application.");
        LOG.info("-username - Local computer account name under which the AMS application is running.");
        LOG.info("-pw - Password that is needed to stop an application.");
        LOG.info("[-help | -?] - Print this text. All other parameters will be ignored.");
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
        // Nothing to do here
    }
    
    @Override
    public void bindService(ISessionService sessionService) {
    	
        IPreferencesService pref = Platform.getPreferencesService();
        String xmppServer = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_SERVER, "localhost", null);
        String xmppUser = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_PASSWORD, "anonymous", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppSession = sessionService;
		} catch (Exception e) {
			LOG.warn("XMPP connection is not available: " + e.getMessage());
			xmppSession = null;
		}
    }
    
    @Override
    public void unbindService(ISessionService service) {
    	// Nothing to do here
    }
}
