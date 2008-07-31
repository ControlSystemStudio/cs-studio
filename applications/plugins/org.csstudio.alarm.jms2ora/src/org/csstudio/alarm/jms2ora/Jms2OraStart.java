
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.alarm.jms2ora;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The starting class.
 * 
 * @author Markus Möller
 *
 */

public class Jms2OraStart implements IApplication
{
    /**  */
    public static boolean SHUTDOWN = true;
    
    /**  */
    private MessageProcessor applic = null;
    
    /**  */
    private Logger logger = null;
    
    public Jms2OraStart()
    {
        createLogger();
    }
    
    private boolean createLogger()
    {
        boolean result = false;
        
        PropertyConfigurator.configure("log4j-jms2ora.properties");
        
        logger = Logger.getLogger(Jms2OraStart.class);
        
        return result;
    }

    public Object start(IApplicationContext context) throws Exception
    {        
        // Create an object from this class
        applic = MessageProcessor.getInstance();

        // Was the initialization successful?
        if(applic.isInitialized() == true)
        {
            // Start...
            applic.run();
        }
        else // Sorry, some errors were caused...
        {
            logger.error("Could not initialize the class 'StoreMessages' -> Shutting down.");
            
            try
            {
                Thread.sleep(5000);
            }
            catch(InterruptedException e) { }
        }
        
        // Clean up...
        applic.closeAllReceivers();
        
        if(SHUTDOWN)
        {
            return IApplication.EXIT_OK;
        }
        else
        {
            return IApplication.EXIT_RESTART;
        }
    }

    public void stop()
    {
        return;
    }
}