
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

package org.csstudio.alarm.jms2ora.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class MessageStorage implements Runnable
{
    /** the class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageStorage.class);

    /** Object for database handling */
    private DatabaseLayer dbLayer;

    /** Queue for received messages */
    private ConcurrentLinkedQueue<MessageContent> messages = null;
    
    /** Object that is used for waiting */
    private Object waitObject;
    
    /** Flag that indicates if this thread is working */
    private boolean working;
    
    private final long WAIT_TIME = 30000;
    
    public MessageStorage(String url, String user, String password)
    {
        messages = new ConcurrentLinkedQueue<MessageContent>();
        dbLayer = new DatabaseLayer(url, user, password);
        waitObject = new Object();
        working = true;
    }
    
    public void run()
    {
        MessageContent[] content = null;
        boolean forceStorage = false;
        
        while(working)
        {
            synchronized(waitObject)
            {
                try
                {
                    waitObject.wait(WAIT_TIME);
                    
                    // Force storage every WAIT_TIME ms
                    forceStorage = true;
                }
                catch(InterruptedException ie)
                {
                    LOG.info(" *** INTERRUPTED *** ");
                }
            }
            
            if(messages.isEmpty() == false)
            {
                LOG.debug("Number of unstored messages: ", messages.size());
                
                if((messages.size() >= 10) || forceStorage)
                {
                    content = new MessageContent[messages.size()];
                    messages.toArray(content);
                    
                    
                    // Store the message in a file, if it was not possible to write it to the DB.
                    // MessageFileHandler.getInstance().writeMessageContentToFile(content);

                    forceStorage = false;
                }
            }
        }
    }
    
    public void addMessage(MessageContent mc)
    {
        messages.add(mc);
        
        synchronized(waitObject)
        {
            waitObject.notify();
        }
    }
    
    public void stopWorking()
    {
        working = false;
        
        synchronized(waitObject)
        {
            waitObject.notify();
        }
    }
}
