
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

import java.io.File;
import org.apache.log4j.Logger;
import org.csstudio.alarm.jms2ora.util.ApplicState;
import org.csstudio.alarm.jms2ora.util.SynchObject;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The starting class.
 * 
 * @author Markus Möller
 *
 */

public class Jms2OraApplication implements IApplication
{
    private static Jms2OraApplication instance = null;
    
    /** The MessageProcessor does all the work on messages */
    private MessageProcessor messageProcessor = null;
    
    /** Log4j logger */
    private Logger logger = null;
    
    /**  */
    private SynchObject sync = null;
    
    /** Name of the folder that holds the stored message content */
    private final String objectDir = ".\\var\\";

    /** Last state */
    private int lastState = 0;
    
    /** Flag that indicates whether or not the application is/should running */
    private boolean running = true;
    
    /** Flag that indicates whether or not the application should stop. */
    public boolean shutdown = false;
    
    /** Time to sleep in ms */
    private static long SLEEPING_TIME = 60000 ;

    /** Time to sleep in ms */
    private long WAITFORTHREAD = 20000 ;

    public Jms2OraApplication()
    {
        instance = this;
        logger = Logger.getLogger(Jms2OraApplication.class);
        createObjectFolder();
    
        sync = new SynchObject(ApplicState.INIT, System.currentTimeMillis());
    }
    
    public static Jms2OraApplication getInstance()
    {
        return instance;
    }
    

    public Object start(IApplicationContext context) throws Exception
    {
        String stateText = null;
        int currentState = 0;
        
        // Create an object from this class
        messageProcessor = MessageProcessor.getInstance();
        messageProcessor.setParent(this);
        messageProcessor.start();

        sync.setSynchStatus(ApplicState.OK);
        stateText = "ok";
        
        while(running)
        {
            synchronized(this)
            {
                try
                {
                    this.wait(SLEEPING_TIME);
                }
                catch(InterruptedException ie) {}
            }
            
            SynchObject actSynch = new SynchObject(ApplicState.INIT, 0);
            if(!sync.hasStatusSet(actSynch, 300, ApplicState.TIMEOUT))    
            {
                logger.fatal("TIMEOUT: State has not changed the last 5 minute(s).");
            }

            currentState = actSynch.getStatus();
            if(currentState != lastState)
            {
                switch(currentState)
                {
                    case ApplicState.INIT:
                        stateText = "init";
                        break;
                        
                    case ApplicState.OK:
                        stateText = "ok";
                        break;
                        
                    case ApplicState.WORKING:
                        stateText = "working";
                        break;

                    case ApplicState.SLEEPING:
                        stateText = "sleeping";
                        break;

                    case ApplicState.LEAVING:
                        stateText = "leaving";
                        break;

                    case ApplicState.ERROR:
                        stateText = "error";                        
                        running = false;                        
                        break;
                        
                    case ApplicState.FATAL:
                        stateText = "fatal";
                        running = false;                        
                        break;
                    
                    case ApplicState.TIMEOUT:
                        stateText = "timeout";
                        running = false;                        
                        break;
                }
                
                logger.debug("set state to " + stateText + "(" + currentState + ")");
                lastState = currentState;               
            }
            
            logger.debug("Current state: " + stateText + "(" + currentState + ")");
        }

        if(messageProcessor != null)
        {
            // Clean stop of the working thread
            messageProcessor.stopWorking();
            
            do
            {
                try
                {
                    messageProcessor.join(WAITFORTHREAD);
                }
                catch(InterruptedException ie) { }
            }
            while(sync.getSynchStatus() == ApplicState.LEAVING);
            
            if(messageProcessor.stoppedClean())
            {
                logger.info("Restart/Exit: Thread stopped clean.");
                
                messageProcessor = null;
            }
            else
            {
                logger.warn("Restart/Exit: Thread did NOT stop clean.");
                messageProcessor = null;
            }
        }
        
        if(shutdown)
        {
            return IApplication.EXIT_OK;
        }
        else
        {
            logger.info("Restarting application...");
            
            return IApplication.EXIT_RESTART;
        }
    }
    
    public int getState()
    {
        return sync.getSynchStatus();
    }
    
    public void setStatus(int status)
    {
        sync.setSynchStatus(status);
    }

    public void setShutdown()
    {
        running = false;
        shutdown = true;
        
        logger.info("The application will shutdown...");
        
        synchronized(this)
        {
            notify();
        }
    }

    public void setRestart()
    {
        running = false;
        shutdown = false;
        
        logger.info("The application will restart...");
        
        synchronized(this)
        {
            notify();
        }
    }

    public void stop()
    {
        return;
    }
    
    private void createObjectFolder()
    {
        File folder = new File(objectDir);
                
        if(!folder.exists())
        {
            boolean result = folder.mkdir();
            if(result)
            {
                logger.info("Folder " + objectDir + " was created.");                
            }
            else
            {
                logger.warn("Folder " + objectDir + " was NOT created.");
            }
        }
    }
}