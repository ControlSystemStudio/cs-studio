
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: MessageMinderStart.java,v 1.11 2010/04/16 14:07:27 mmoeller Exp $
 */

package org.csstudio.ams.messageminder;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.ams.messageminder.preference.MessageMinderPreferenceKey;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * @author hrickens
 * @author $Author: mmoeller $
 * @version $Revision: 1.11 $
 * @since 01.11.2007
 */
public final class MessageMinderStart implements IApplication, IGenericServiceListener<ISessionService> {

    private boolean _restart = false;
    // private boolean _run = true;
    private MessageGuardCommander _commander;
    private static MessageMinderStart _instance;
    private ISessionService xmppService;
    public final static boolean CREATE_DURABLE = true;
    private String managementPassword;

    public MessageMinderStart()
    {
        IPreferencesService pref = Platform.getPreferencesService();
        managementPassword = pref.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_AMS_MANAGEMENT_PASSWORD, "", null);
        if(managementPassword == null) {
            managementPassword = "";
        }
        
        xmppService = null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start(IApplicationContext context) throws Exception {
        _instance = this;
        
        MessageMinderPreferenceKey.showPreferences();
        
        CentralLogger.getInstance().info(this, "MessageMinder started...");

        _commander = new MessageGuardCommander("MessageMinder");
        _commander.schedule();
        
        while(_commander.getState()!=Job.NONE){
            CentralLogger.getInstance().info(this, "Commander state = " + String.valueOf(_commander.getState()));
            Thread.sleep(10000);
        }
        _commander.cancel();
        
        if (xmppService != null) {
            xmppService.disconnect();
        }
        
        Integer exitCode = IApplication.EXIT_OK;
        if(_restart){
            exitCode = IApplication.EXIT_RESTART;
        }
        
        return exitCode;
    }

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        // Do nothing here
    }

    public boolean isRestart() {
        return _restart;
    }

    public synchronized void setRestart() {
        _restart = true;
        setRun(false);
    }


    public synchronized void setRun(boolean run) {
        if(_commander!=null){
            _commander.setRun(run);
        }
    }

    /**
     *  
     * @return The password for remote management
     */
    public synchronized String getPassword() {
        return managementPassword;
    }
    
    public static MessageMinderStart getInstance() {
        return _instance;
    }
    

    public void bindService(ISessionService sessionService) {
    	IPreferencesService pref = Platform.getPreferencesService();
    	String xmppServer = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = pref.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_PASSWORD, "anonymous", null);

    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppService = sessionService;
		} catch (Exception e) {
		    Log.log(this, Log.WARN, "XMPP connection is not available: " + e.getMessage());
		}
    }
    
    public void unbindService(ISessionService service) {
        // Nothing to do here
    }
    
}
