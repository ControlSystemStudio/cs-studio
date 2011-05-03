
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.ams.remotetool.internal.PreferenceKeys;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandDescription;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommandService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.login.connection.HeadlessConnection;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * @author Markus Moeller
 *
 */
public class AmsRemoteTool implements IApplication, IGenericServiceListener<ISessionService>
{
    /** Command line helper */
    private CommandLine cl;
    
    /** The logger */
    private CentralLogger logger;
    
    private ISessionService session;
    
    public AmsRemoteTool()
    {
        cl = null;
        logger = CentralLogger.getInstance();
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    public Object start(IApplicationContext context) throws Exception
    {
        IRoster roster = null;
        Vector<IRosterItem> rosterItems = null;
        Vector<IRosterEntry> rosterEntries = null;
        IRosterGroup jmsApplics = null;
        IRosterEntry currentApplic = null;
        CommandParameters parameter = null;
        CommandDescription stopAction = null;
        String applicName = null;
        String name = null;
        String user = null;
        String host = null;
        String pw = null;
        int iResult = ApplicResult.RESULT_ERROR_GENERAL.ordinal();
        
        String[] args = (String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        cl = new CommandLine(args);
        
        logger.info(this, "AmsRemoteTool started...");
        
        if(cl.exists("help") || cl.exists("?"))
        {
            usage();
            return iResult;
        }
        
        // Check the command line arguments
        // We expect:
        // -applicname - Name of the application to stop
        // -host - Name of the computer on which the application runs
        // -username - Name of the user
        // -pw - Password for stopping
        if(!cl.exists("host") || !cl.exists("applicname") || !cl.exists("username") || !cl.exists("pw"))
        {
            logger.error(this, "One or more application arguments are missing.");
            usage();
            return iResult;
        }
                
        applicName = cl.value("applicname");
        host = cl.value("host");
        user = cl.value("username");
        pw = cl.value("pw");
        
        logger.info(this, "Try to stop " + applicName + " on host " + host + ". Running under the account: " + user);
        
        connectToXMPPServer();

        session = OsgiServiceLocatorUtil.getOSGiService(Activator.getDefault().getBundle().getBundleContext(), ISessionService.class);

        int count = 0;
  
        // We have to wait until the DCF connection manager have been initialized
        synchronized(this)
        {
            do
            {
                try
                {
                    this.wait(2000);
                }
                catch(InterruptedException ie)
                {
                    logger.error(this, "*** InterruptedException ***: " + ie.getMessage());
                }
  
                roster = session.getRoster();

                // Get the roster items
                rosterItems = new Vector<IRosterItem>((Collection<IRosterItem>)roster.getItems());
            }
            while((++count <= 10) && (rosterItems.isEmpty()));
        }

        // We have to wait until the DCF connection manager have been initialized
        synchronized(this)
        {
            try
            {
                this.wait(2000);
            }
            catch(InterruptedException ie)
            {
                logger.error(this, "*** InterruptedException ***: " + ie.getMessage());
            }
        }
        
        if(rosterItems.size() == 0)
        {
            logger.info(this, "XMPP roster not found. Stopping application.");
            return ApplicResult.RESULT_ERROR_XMPP.ordinal();
        }
        else
        {
            logger.info(this, "Manager initialized");
        }
        
        logger.info(this, "Anzahl Directory-Elemente: " + rosterItems.size());
        
        // Get the group of JMS applications
        for(IRosterItem ri : rosterItems)
        {
            if(ri.getName().compareToIgnoreCase("jms-applications") == 0)
            {
                jmsApplics = (IRosterGroup)ri;
                break;
            }
        }
        
        // Get the application container
        if(jmsApplics != null)
        {
            rosterEntries = new Vector<IRosterEntry>((Collection<IRosterEntry>)jmsApplics.getEntries());
            
            Iterator<IRosterEntry> list = rosterEntries.iterator();
            while(list.hasNext())
            {
                IRosterEntry ce = list.next();
                name = ce.getUser().getID().toExternalForm();
                if(name.contains(applicName))
                {
                    if((name.indexOf(host) > -1) && (name.indexOf(user) > -1))
                    {
                        currentApplic = ce;
                        break;
                    }
                }
            }
        }
        else
        {
            iResult = ApplicResult.RESULT_ERROR_UNKNOWN.ordinal();
        }
        
        IManagementCommandService service = null;
        
        if(currentApplic != null)
        {
            logger.info(this, "Anwendung gefunden: " + currentApplic.getUser().getID().getName());
            
            List<IManagementCommandService> managementServices =
                session.getRemoteServiceProxies(
                    IManagementCommandService.class, new ID[] {currentApplic.getUser().getID()});
            
            if (managementServices.size() == 1)
            {
                service = managementServices.get(0);
                CommandDescription[] commands = service.getSupportedCommands();
                
                for (int i = 0; i < commands.length; i++)
                {
                    if(commands[i].getLabel().compareToIgnoreCase("stop") == 0)
                    {
                        stopAction = commands[i];
                        break;
                    }
                }
            }
            
            if(stopAction != null)
            {
                parameter = new CommandParameters();
                parameter.set("Password", pw);
                
                CommandResult retValue = service.execute(stopAction.getIdentifier(), parameter);
                if(retValue != null)
                {
                    String result = (String)retValue.getValue();
                    if((result.trim().startsWith("OK:")) || (result.indexOf("stopping") > -1))
                    {
                        logger.info(this, "Application stopped: " + result);
                        iResult = ApplicResult.RESULT_OK.ordinal();
                    }
                    else
                    {
                        logger.error(this, "Something went wrong: " + result);
                        iResult = ApplicResult.RESULT_ERROR_INVALID_PASSWORD.ordinal();
                    }
                }
                else
                {
                    logger.info(this, "Return value is null!");
                    iResult = ApplicResult.RESULT_ERROR_UNKNOWN.ordinal();
                }
            }
        }        
        else
        {
            iResult = ApplicResult.RESULT_ERROR_NOT_FOUND.ordinal();
        }

        return iResult;
    }
    
    public void connectToXMPPServer()
    {
        IPreferencesService pref = Platform.getPreferencesService();
        String xmppServer = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_SERVER, "localhost", null);
        String xmppUser = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(Activator.PLUGIN_ID, PreferenceKeys.P_XMPP_PASSWORD, "anonymous", null);

        try
        {
            HeadlessConnection.connect(xmppUser, xmppPassword, xmppServer, ECFConstants.XMPP);
            ServiceLauncher.startRemoteServices();     
        }
        catch(Exception e)
        {
            CentralLogger.getInstance().warn(this, "Could not connect to XMPP server: " + e.getMessage());
        }
    }

    public void usage()
    {
        logger.info(this, "AmsRemoteTool, Markus Moeller, MKS 2, (C)2010");
        logger.info(this, "This application stops an AMS process via XMPP action call.");
        logger.info(this, "Options:");
        logger.info(this, "-host - Name of the computer on which the AMS application is running.");
        logger.info(this, "-applicname - XMPP account name of the AMS application.");
        logger.info(this, "-username - Local computer account name under which the AMS application is running.");
        logger.info(this, "-pw - Password that is needed to stop an application.");
        logger.info(this, "[-help | -?] - Print this text. All other parameters will be ignored.");
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop()
    {

    }
    

    public void bindService(ISessionService sessionService) {
    	final String username = getValueFromPreferences(XMPP_USER, "anonymous");
    	final String password = getValueFromPreferences(XMPP_PASSWD, "anonymous");
    	final String server = getValueFromPreferences(XMPP_SERVER, "krynfs.desy.de");
    	
    	try {
			sessionService.connect(username, password, server);
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
    
}
