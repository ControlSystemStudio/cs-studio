
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

package org.csstudio.ams.connector.sms;

import java.util.Vector;

/**
 * @author Markus Moeller
 *
 */
public class ModemTestStatus
{
    private Vector<String> gateway;
    private Vector<String> badModem;
    private String timeStamp;
    private String answerEventTime;
    private long timeOut;
    private boolean active;
    
    public static final String SMS_TEST_TEXT = "[MODEMTEST{$DATE,$GATEWAYID}]";
    
    public ModemTestStatus()
    {
        gateway = new Vector<String>();
        badModem = new Vector<String>();
        
        reset();
    }
    
    public void reset()
    {
        gateway.clear();
        badModem.clear();
        timeStamp = "";
        answerEventTime = "";
        timeOut = 0;
        active = false;
    }
    
    public void checkAndRemove(String content)
    {
        String text = null;
        
        for(String name : gateway)
        {
            text = SMS_TEST_TEXT;
            text = text.replaceAll("\\$DATE", timeStamp);
            text = text.replaceAll("\\$GATEWAYID", name);
            
            if(content.compareTo(text) == 0)
            {
                gateway.removeElement(name);
                break;
            }
        }
    }
    
    public void moveGatewayIdToBadModems()
    {
        for(String name : gateway)
        {
            if(badModem.contains(name) == false)
            {
                badModem.add(name);
            }
        }
    }
    
    public boolean isTestAnswer(String text)
    {
        return (text.startsWith("[MODEMTEST{"));
    }
    
    /**
     * @return the timeStamp
     */
    public String getTimeStamp()
    {
        return timeStamp;
    }
    
    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }
    
    public String getAnswerEventTime()
    {
        return answerEventTime;
    }
    
    /**
     * @param timeStamp the timeStamp to set
     */
    public void setAnswerEventTime(String eventTime)
    {
        this.answerEventTime = eventTime;
    }
    
    public boolean containsGatewayId(String name)
    {
        return gateway.contains(name);
    }
    
    public void removeGatewayId(String name)
    {
        if(gateway.contains(name))
        {
            gateway.remove(name);
        }
    }
    
    /**
     * @param gateway the gateway to set
     */
    public void addGatewayId(String gateway)
    {
        this.gateway.add(gateway);
    }
    
    public int getGatewayCount()
    {
        return gateway.size();
    }
    
    public void addBadModem(String name)
    {
        if(badModem.contains(name) == false)
        {
            badModem.add(name);
        }
    }
    
    public String[] getBadModems()
    {
        String[] result = new String[badModem.size()];
        
        result = badModem.toArray(result);
        
        return result;
    }
    
    public int getBadModemCount()
    {
        return badModem.size();
    }

    public void setTimeOut(long timeOut)
    {
        this.timeOut = timeOut;
    }
    
    public long getTimeOut()
    {
        return timeOut;
    }

    public boolean isTimeOut()
    {
        return (System.currentTimeMillis() > timeOut);
    }
    
    /**
     * @return the active
     */
    public boolean isActive()
    {
        return active;
    }
    /**
     * @param active the active to set
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
