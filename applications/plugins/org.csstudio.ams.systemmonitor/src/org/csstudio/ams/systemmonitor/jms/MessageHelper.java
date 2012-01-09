
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

package org.csstudio.ams.systemmonitor.jms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.csstudio.ams.systemmonitor.check.CheckResult;
import org.csstudio.ams.systemmonitor.status.MonitorStatusEntry;
import org.csstudio.ams.systemmonitor.util.Environment;

/**
 * @author Markus Moeller
 *
 */
public class MessageHelper
{
    private Hashtable<String, String> message;
    private SimpleDateFormat dateFormat;
    private String errorText;
        
    public MessageHelper()
    {
        message = new Hashtable<String, String>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        errorText = "undefined";
    }

    public Hashtable<String, String> getNewCheckMessage(MessageType type, MonitorStatusEntry currentStatusEntry)
    {
        message.clear();
        
        message.put("TYPE", "event");
        message.put("EVENTTIME", dateFormat.format(Calendar.getInstance().getTime()));
        message.put("CLASS", currentStatusEntry.getCheckId());
        message.put("NAME", "AMS_SYSTEM_CHECK");
        message.put("APPLICATION-ID", "AmsSystemMonitor");
        message.put("DESTINATION", type.toString());        
        message.put("USER", Environment.getInstance().getUserName());
        message.put("HOST", Environment.getInstance().getHostName());
        message.put("SEVERITY", "NO_ALARM");
        message.put("STATUS", "NO_ALARM");
        
        return message;
    }
    
    public boolean isAmsAnswer(Hashtable<String, String> content, MapMessage mapMessage)
    {
        Hashtable<String, String> receivedMessageContent = null;
        String value = null;
        boolean amsAnswer = false;
        
        receivedMessageContent = extractContent(mapMessage);
        if(receivedMessageContent.containsKey("CLASS") && content.containsKey("CLASS"))
        {
            value = receivedMessageContent.get("CLASS");
            
            amsAnswer = (content.get("CLASS").compareTo(value) == 0);
        }
        else
        {
            amsAnswer = false;
        }
        
        if(receivedMessageContent.containsKey("NAME") && amsAnswer)
        {
            value = receivedMessageContent.get("NAME");
            amsAnswer = (value.compareTo("AMS_SYSTEM_CHECK") == 0);
        }
        else
        {
            amsAnswer = false;
        }

        if(receivedMessageContent.containsKey("APPLICATION-ID") && amsAnswer)
        {
            value = receivedMessageContent.get("APPLICATION-ID");
            amsAnswer = (value.compareTo("AmsSystemMonitor") == 0);
        }
        else
        {
            amsAnswer = false;
        }
        
        if(receivedMessageContent.containsKey("DESTINATION") && amsAnswer)
        {
            value = receivedMessageContent.get("DESTINATION");
            amsAnswer = (value.compareTo("System") == 0);
        }
        else
        {
            amsAnswer = false;
        }

        return amsAnswer;
    }
    
    public boolean isAnswerFromSmsConnector(MapMessage mapMsg)
    {
        Hashtable<String, String> answer = null;
        String value;
        boolean success = false;
        
        answer = extractContent(mapMsg);
        
        // Check the content
        // The message has to contain this properties:
        //  NAME = AMS_SYSTEM_CHECK_ANSWER
        //  APPLICATION-ID = SmsConnector
        //  DESTINATION = AmsSystemMonitor
        //  TEXT = OK | ERROR
        //  CLASS = <check id>
        if(answer.isEmpty() == false)
        {            
            if(answer.containsKey("NAME"))
            {
                value = answer.get("NAME");
                success = (value.compareTo("AMS_SYSTEM_CHECK_ANSWER") == 0);
            }

            if(answer.containsKey("APPLICATION-ID") && success)
            {
                value = answer.get("APPLICATION-ID");
                success = (value.compareTo("SmsConnector") == 0);
            }
            else
            {
                success = false;
            }
            
            if(answer.containsKey("DESTINATION") && success)
            {
                value = answer.get("DESTINATION");
                success = (value.compareTo("AmsSystemMonitor") == 0);
            }
            else
            {
                success = false;
            }
        }
        
        return success;
    }
    
    public boolean isAnswerFromSmsConnector(Hashtable<String, String> content)
    {
        String value;
        boolean success = false;
        
        // Check the content
        // The message has to contain this properties:
        //  NAME = AMS_SYSTEM_CHECK_ANSWER
        //  APPLICATION-ID = SmsConnector
        //  DESTINATION = AmsSystemMonitor
        //  TEXT = OK | ERROR
        
        if(content.isEmpty() == false)
        {            
            if(content.containsKey("NAME"))
            {
                value = content.get("NAME");
                success = (value.compareTo("AMS_SYSTEM_CHECK_ANSWER") == 0);
            }

            if(content.containsKey("APPLICATION-ID") && success)
            {
                value = content.get("APPLICATION-ID");
                success = (value.compareTo("SmsConnector") == 0);
            }
            else
            {
                success = false;
            }
            
            if(content.containsKey("DESTINATION") && success)
            {
                value = content.get("DESTINATION");
                success = (value.compareTo("AmsSystemMonitor") == 0);
            }
            else
            {
                success = false;
            }
        }
        
        return success;
    }

    public CheckResult getAnswerFromSmsConnector(MapMessage mapMsg, Hashtable<String, String> sentMessage)
    {
        Hashtable<String, String> answer = null;
        CheckResult result = CheckResult.NONE;
        String value = null;
        boolean success = false;
        
        answer = extractContent(mapMsg);
        
        if(isAnswerFromSmsConnector(answer) == false)
        {
            return CheckResult.NONE;
        }
        
        // Check the content
        // The message has to contain this properties:
        //  NAME = AMS_SYSTEM_CHECK_ANSWER
        //  APPLICATION-ID = SmsConnector
        //  DESTINATION = AmsSystemMonitor
        //  TEXT = OK | ERROR
        
        if(answer.isEmpty() == false)
        {
            if(answer.containsKey("CLASS") && sentMessage.containsKey("CLASS"))
            {
                value = answer.get("CLASS");
                
                success = (sentMessage.get("CLASS").compareTo(value) == 0);
            }
            else
            {
                success = false;
            }

            if(success == false)
            {
                return CheckResult.NONE;
            }
                        
            if(answer.containsKey("VALUE") && success)
            {
                value = answer.get("VALUE");
                if(value.compareToIgnoreCase("OK") == 0)
                {
                    result = CheckResult.OK;
                    success = true;
                }
                else if(value.compareToIgnoreCase("WARN") == 0)
                {
                    result = CheckResult.WARN;
                    success = true;
                }
                else if(value.compareToIgnoreCase("ERROR") == 0)
                {
                    result = CheckResult.ERROR;
                    success = true;
                }
            }
            else
            {
                success = false;
            }
            
            if(answer.containsKey("TEXT") && success)
            {
                errorText = answer.get("TEXT");
            }
            else
            {
                errorText = "Undefined";
            }
        }

        return result;
    }
    
    public String getErrorText() {
        return errorText;
    }
    
    public Hashtable<String, String> extractContent(MapMessage mapMsg) {
        Hashtable<String, String> result = new Hashtable<String, String>();
        Enumeration<?> keys = null;
        String key = null;
        
        try
        {
            keys = mapMsg.getMapNames();
            while(keys.hasMoreElements())
            {
                key = (String)keys.nextElement();
                result.put(key, mapMsg.getString(key));
            }
        }
        catch(JMSException jmse)
        {
            result.clear();
        }
        
        return result;
    }
    
    /**
     * 
     * @author Markus Moeller
     *
     */
    public enum MessageType {
        
        SYSTEM("System"),
        SMS_DELIVERY_WORKER("SmsDeliveryWorker"),
        ALL_DELIVERY_WORKER("*");
        
        private String name;
        
        private MessageType(String n) {
            this.name = n;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
//    public boolean compareMessages(Hashtable<String, String> content, MapMessage mapMessage)
//    {
//        Hashtable<String, String> receivedMessageContent = null;
//        String key = null;
//        String value = null;
//        String compare = null;
//        boolean isEqual = false;
//        
//        receivedMessageContent = extractContent(mapMessage);
//        if(receivedMessageContent.isEmpty() == false)
//        {
//            // Compare the hash codes. They have to be equal
//            if(content.hashCode() == receivedMessageContent.hashCode())
//            {
//                isEqual = true;
//            }
//            else
//            {
//                // May be the hash codes are not equal but the messages may be equal anyhow
//                Enumeration<?> keyList = content.keys();
//                while(keyList.hasMoreElements())
//                {
//                    key = (String)keyList.nextElement();
//                    value = content.get(key);
//                    if(receivedMessageContent.containsKey(key))
//                    {
//                        compare = receivedMessageContent.get(key);
//                        if(value.compareTo(compare) != 0)
//                        {
//                            isEqual = false;
//                            break;
//                        }
//                        else
//                        {
//                            isEqual = true;
//                        }
//                    }
//                    else
//                    {
//                        // The key is not present, then the messges are not equal.
//                        isEqual = false;
//                        break;
//                    }
//                }
//            }
//        }
//        
//        return isEqual;
//    }
}
