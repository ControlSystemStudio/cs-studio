
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

public class SimpleStatistic
{
    private static SimpleStatistic instance;
    
    private long numberOfReceivedMessages;
    private long numberOfStoredMessages;
    private long numberOfDiscardedMessages;
    private long numberOfEmptyMessages;
    
    private SimpleStatistic()
    {
        numberOfReceivedMessages = 0;
        numberOfStoredMessages = 0;
    }
    
    public static synchronized SimpleStatistic getInstance()
    {
        if(instance == null)
        {
            instance = new SimpleStatistic();
        }
        
        return instance;
    }
    
    public synchronized void incrementNumberOfReceivedMessages()
    {
        numberOfReceivedMessages++;
    }
    
    public synchronized void incrementNumberOfStoredMessages()
    {
        numberOfStoredMessages++;
    }

    public synchronized void incrementNumberOfEmptyMessages()
    {
        numberOfEmptyMessages++;
    }
    
    public synchronized void incrementNumberOfDiscardedMessages()
    {
        numberOfDiscardedMessages++;
    }

    public synchronized long getNumberOfReceivedMessages()
    {
        return numberOfReceivedMessages;
    }

    public synchronized long getNumberOfDiscardedMessages()
    {
        return numberOfDiscardedMessages;
    }
    
    public synchronized long getNumberOfEmptyMessages()
    {
        return numberOfEmptyMessages;
    }

    public synchronized long getNumberOfStoredMessages()
    {
        return numberOfStoredMessages;
    }
    
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        
        result.append("Statistic:\n\n");
        result.append("Received Messages:  " + numberOfReceivedMessages + "\n");
        result.append("Stored Messages:    " + numberOfStoredMessages + "\n");
        result.append("Discarded Messages: " + numberOfDiscardedMessages + "\n");
        result.append("Empty Messages:     " + numberOfEmptyMessages + "\n");
        
        return result.toString();
    }
}
