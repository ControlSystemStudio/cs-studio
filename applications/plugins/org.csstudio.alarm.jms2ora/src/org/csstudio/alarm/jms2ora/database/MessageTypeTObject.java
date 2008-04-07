
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
 *
 */

package org.csstudio.alarm.jms2ora.database;

import java.util.ArrayList;

/**
 * The class <code>MessageTypeTObject</code> is a representation of a message type.
 * It contains the type and the properties of a message that is stored in the 
 * alarm database.
 * 
 * @author Markus Moeller
 * @version 1.0
 * 
 */
public class MessageTypeTObject
{
    /** Type of message that is represented by this object. */
    private String messageType = null;
    
    /** All possible properties of the message type */
    private ArrayList<String> messageProperties = null;
    
    /**
     * Constructs an object with the given message type and properties.
     */
    public MessageTypeTObject(String type, String[] properties)
    {
        this.messageType = type;
        setMessageProperties(properties);
    }
    
    /**
     * Returns the type of the message.
     * 
     * @return String containing the message type.
     */
    public String getMessageType()
    {
        return messageType;
    }
    
    /**
     * Sets the message type.
     * 
     * @param messageType String containing the message type
     */
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }
    
    /**
     * Returns an array with all properties.
     * 
     * @return Array of String containing all properties of the message type
     */
    public String[] getMessageProperties()
    {
        String[] result = null;
        
        if(messageProperties != null)
        {
            result = new String[messageProperties.size()];
            messageProperties.toArray(result);
        }
        
        return result;
    }
    
    /**
     * Sets the properties for the message type. The old array containing the properties
     * will be overwritten.
     * 
     * @param properties Array of String that contains all property names.
     */
    public void setMessageProperties(String[] properties)
    {
        if(this.messageProperties != null)
        {
            this.messageProperties.clear();
            this.messageProperties = null;
        }
        
        this.messageProperties = new ArrayList<String>(properties.length);

        for(int i = 0;i < properties.length;i++)
        {
            this.messageProperties.add(properties[i]);
        }
    }
    
    /**
     * Adds the property to the list of property names.
     * 
     * @param property
     */
    public void addProperty(String property)
    {
        if(this.messageProperties == null)
        {
            this.messageProperties = new ArrayList<String>();
        }
        
        this.messageProperties.add(property);
    }
    
    /**
     * Return <code>true</code> if the property list contains the specified element.
     * 
     * @param name Name of the property
     * @return <code>true</code> if the specified element is present; <code>false</code> otherwise.
     */
    public boolean containsProperty(String name)
    {
        return this.messageProperties.contains(name);
    }
}
