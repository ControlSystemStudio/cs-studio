
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.message;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.csstudio.utility.jms.JmsMapMessageBuilder;

/**
 * @author mmoeller
 * @version 1.0
 * @since 12.04.2012
 */
public class MessageConverter {
    
    private Hashtable<String, String> methodMap;
    
    public MessageConverter() {
        methodMap = new Hashtable<String, String>();
        initMethodMap();
    }
    
    private void initMethodMap() {
        methodMap.put("TYPE", "setTypeValue");
        methodMap.put("EVENTTIME", "setEventTimeValue");
        methodMap.put("NAME", "setNameValue");
        methodMap.put("CLASS", "setClassValue");
        methodMap.put("HOST", "setHostValue");
        methodMap.put("SEVERITY", "setSeverityValue");
        methodMap.put("STATUS", "setStatusValue");
        methodMap.put("APPLICATION-ID", "setApplicationIdValue");
        methodMap.put("USER", "setUserValue");
        methodMap.put("DESTINATION", "setDestinationValue");
        methodMap.put("TEXT", "setTextValue");
        methodMap.put("VALUE", "setValue");
    }
    
    public AmsAnswerMessage convertToAmsAnswerMessage(MapMessage message) {
        
        AmsAnswerMessage result = new AmsAnswerMessage();
        
        try {
            Enumeration<?> keys = message.getMapNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (methodMap.containsKey(key)) {
                    String methodName = methodMap.get(key);
                    try {
                        Method method = result.getClass().getMethod(methodName, new Class<?>[] {String.class});
                        method.invoke(result, message.getString(key));
                    } catch (Exception e) {
                        result.reset();
                        break;
                    }
                } else {
                    break;
                }
            }
            
            boolean valid = true;
            
            // Every getter must return a value != null
            Method[] methods = result.getClass().getMethods();
            for (Method m : methods) {
                String methodName = m.getName();
                if ((methodName.startsWith("get")) && (methodName.endsWith("Value"))) {
                    try {
                        Object o = m.invoke(result, (Object[]) null);
                        if (o == null) {
                            valid = false;
                            break;
                        }
                    } catch (Exception e) {
                        result.reset();
                    }
                }
            }
            
            if (valid) {
                result.setValid(valid);
            } else {
                result.reset();
            }
            
        } catch (JMSException e) {
            result.reset();
        }
        
        return result;
    }
    
    public DeliveryWorkerAnswerMessage convertToDeliveryWorkerAnswerMessage(MapMessage message) {
        
        DeliveryWorkerAnswerMessage result = new DeliveryWorkerAnswerMessage();
        
        try {
            Enumeration<?> keys = message.getMapNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (methodMap.containsKey(key)) {
                    String methodName = methodMap.get(key);
                    try {
                        Method method = result.getClass().getMethod(methodName, new Class<?>[] {String.class});
                        method.invoke(result, message.getString(key));
                    } catch (Exception e) {
                        result.reset();
                        break;
                    }
                } else {
                    break;
                }
            }
            
            boolean valid = true;
            
            // Every getter must return a value != null
            Method[] methods = result.getClass().getMethods();
            for (Method m : methods) {
                String methodName = m.getName();
                if ((methodName.startsWith("get")) && (methodName.endsWith("Value"))) {
                    try {
                        Object o = m.invoke(result, (Object[]) null);
                        if (o == null) {
                            valid = false;
                            break;
                        }
                    } catch (Exception e) {
                        result.reset();
                    }
                }
            }
            
            if (valid) {
                result.setValid(valid);
            } else {
                result.reset();
            }
            
        } catch (JMSException e) {
            result.reset();
        }
        
        return result;
    }
    
    public MapMessage convertToMapMessage(InitiatorMessage origin,
                                          Session session) throws JMSException {
        
        JmsMapMessageBuilder builder = new JmsMapMessageBuilder(origin.getTypeValue());
        builder.setName(origin.getNameValue())
               .setClass(origin.getClassValue())
               .setHost(origin.getHostValue())
               .setSeverity(origin.getSeverityValue())
               .setStatus(origin.getStatusValue())
               .setApplicationId(origin.getApplicationIdValue())
               .setUser(origin.getUserValue())
               .setDestination(origin.getDestinationValue());
        
        // The new MapMessage contains a new event time. Remove it!!!
        MapMessage mapMsg = builder.build(session);
        mapMsg.setString("EVENTTIME", origin.getEventTimeValue());
        return mapMsg;
    }
}
