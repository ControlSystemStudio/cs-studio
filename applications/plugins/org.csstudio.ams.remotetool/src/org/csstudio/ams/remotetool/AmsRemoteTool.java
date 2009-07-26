
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.csstudio.platform.libs.dcf.actions.ActionDescriptor;
import org.csstudio.platform.libs.dcf.actions.ActionExecutor;
import org.csstudio.platform.libs.dcf.actions.ActionQueryActions;
import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

/**
 * @author Markus Moeller
 *
 */
public class AmsRemoteTool implements IApplication
{
    /** Command line helper */
    private CommandLine cl;
    
    /** The logger */
    private CentralLogger logger;
    
    private final int RESULT_OK = 0;
    private final int RESULT_ERROR_GENERAL = 1;
    private final int RESULT_ERROR_NOT_FOUND = 2;
    private final int RESULT_ERROR_INVALID_PASSWORD = 3;
    private final int RESULT_ERROR_XMPP = 4;
    private final int RESULT_ERROR_UNKNOWN = 5;

    public AmsRemoteTool()
    {
        cl = null;
        logger = CentralLogger.getInstance();
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start(IApplicationContext context) throws Exception
    {
        ActionDescriptor[] descriptors = null;
        ActionDescriptor stopDescriptor = null;
        ContactElement[] element = null;
        HashMap<String, String> parameter = null;
        String applicInfo = null;
        String applicName = null;
        String user = null;
        String host = null;
        String pw = null;
        int iResult = RESULT_ERROR_GENERAL;
        
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
        
        List<ContactElement> applics = null;
        ContactElement jmsApplic = null;
        
        connectToXMPPServer();

        for(IStartupServiceListener s : StartupServiceEnumerator.getServices())
        {
            s.run();
        }
        
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
                
                // Get the directory
                element = ConnectionManager.getDefault().getDirectory();
            }
            while((++count <= 10) && (element.length == 0));
        }
        
        if(element.length == 0)
        {
            logger.info(this, "XMPP directory not found. Stopping application.");
            return RESULT_ERROR_XMPP;
        }
        else
        {
            logger.info(this, "Manager initialized after " + count + " try/tries.");
        }
        
        logger.info(this, "Anzahl Directory-Elemente: " + element.length);
        
        // Get the group of JMS applications
        for(ContactElement ce : element)
        {
            if(ce.toString().compareToIgnoreCase("jms-applications") == 0)
            {
                jmsApplic = ce;
                break;
            }
        }
        
        // Get the application container
        if(jmsApplic != null)
        {
            applics = jmsApplic.getChildren();
            
            jmsApplic = null;
            
            Iterator<ContactElement> list = applics.iterator();
            while(list.hasNext())
            {
                ContactElement ce = list.next();
                if(ce.toString().startsWith(applicName))
                {
                    jmsApplic = ce;
                    break;
                }
            }
        }
        else
        {
            iResult = this.RESULT_ERROR_UNKNOWN;
        }
        
        // Get the running application
        if(jmsApplic != null)
        {
            applics = jmsApplic.getChildren();
            
            jmsApplic = null;
            
            Iterator<ContactElement> list = applics.iterator();
            while(list.hasNext())
            {
                ContactElement ce = list.next();
                applicInfo = ce.toString();
                if(applicInfo != null)
                {
                    if(applicInfo.startsWith(applicName))
                    {
                        if((applicInfo.indexOf(host) > -1) && (applicInfo.indexOf(user) > -1))
                        {
                            jmsApplic = ce;
                            break;
                        }
                    }
                }
                else
                {
                    logger.info(this, "Application is not running yet.");
                    iResult = RESULT_ERROR_NOT_FOUND;
                }
            }
        }
        else
        {
            iResult = RESULT_ERROR_UNKNOWN;
        }
        
        if(jmsApplic != null)
        {
            logger.info(this, "Anwendung gefunden: isManageable: " + jmsApplic.isManageable());
        
            Object retValue = ActionExecutor.executeObjectSynchronous(ActionQueryActions.ACTION_ID, null, jmsApplic);
            if(retValue instanceof ActionDescriptor[])
            {
                descriptors = (ActionDescriptor[])retValue;
                logger.info(this, "Actions queried: " + descriptors.length);
                
                for(ActionDescriptor ad : descriptors)
                {
                    if(ad.getName().compareToIgnoreCase("stop") == 0)
                    {
                        logger.info(this, ad.getId() + " / " + ad.getName() + " / " + ad.getType());
                        stopDescriptor = ad;
                        break;
                    }
                }
                
                if(stopDescriptor != null)
                {
                    parameter = new HashMap<String, String>();
                    parameter.put("Password", pw);
                    retValue = ActionExecutor.executeObjectSynchronous(stopDescriptor.getId(), parameter, jmsApplic);
                    if(retValue != null)
                    {
                        if(retValue instanceof String)
                        {
                            String result = (String)retValue;
                            if((result.trim().startsWith("OK:")) || (result.indexOf("stopping") > -1))
                            {
                                logger.info(this, "Application stopped: " + result);
                                iResult = RESULT_OK;
                            }
                            else
                            {
                                logger.error(this, "Something went wrong: " + result);
                                iResult = RESULT_ERROR_INVALID_PASSWORD;
                            }
                        }
                        else
                        {
                            logger.info(this, "Unknown return value. Class: " + retValue.getClass().getName());
                            iResult = RESULT_ERROR_UNKNOWN;
                        }
                    }
                    else
                    {
                        logger.info(this, "Return value is null!");
                        iResult = RESULT_ERROR_UNKNOWN;
                    }
                }
            }
        }        
        else
        {
            iResult = RESULT_ERROR_NOT_FOUND;
        }

        return iResult;
    }
    
    public void connectToXMPPServer()
    {
        String xmppUser = "ams-remotetool";
        String xmppPassword = "ams";
        String xmppServer = "krykxmpp.desy.de";

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
        logger.info(this, "AmsRemoteTool, Markus Moeller, MKS 2, (C)2009");
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
}
