
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

package org.csstudio.websuite.dataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.websuite.dataModel.preferences.SeverityMapping;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Message received from the JMS server. The properties of messages are not
 * restricted but the table will only display properties for which a column with
 * the same name is defined.
 * 
 * @author jhatje
 * 
 */
//TODO jhatje: implement new datatypes
public class BasicMessage extends PlatformObject {// implements IProcessVariable {

    /**
     * The properties of the message.
     */
    private HashMap<String, String> _messageProperties = new HashMap<String, String>();



    /**
     * Default constructor
     */
    public BasicMessage() {
        super();
    }

    /**
     * Constructor with initial message properties of the table columns.
     */
    public BasicMessage(String[] propNames) {
        this();
        for (int i = 0; i < propNames.length; i++) {
            _messageProperties.put(propNames[i].split(",")[0], ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public BasicMessage(Map<String, String> messageProperties) {
        _messageProperties.putAll(messageProperties);
    }

    /**
     * Set value of a message property
     * 
     * @param property
     * @param value
     */
    public void setProperty(String property, String value) {
//        if (messageProperties.containsKey(property)) {
            _messageProperties.put(property, value);
//        }
    }

    /**
     * Returns value of the requested property
     * 
     * @param property
     * @return
     */
    public String getProperty(String property) {

        // if the table asks for the severity we return the severity value
        // set in the preferences
        if (property.equals("SEVERITY")) { //$NON-NLS-1$
            if (_messageProperties.get("SEVERITY") != null) { //$NON-NLS-1$
                return SeverityMapping.findSeverityValue(_messageProperties
                        .get("SEVERITY"));
            }
        }

        // to get the severity key (the 'real' severity get from the map
        // message)
        // we have to ask for 'SEVERITY_KEY'
        if (property.equals("SEVERITY_KEY")) { //$NON-NLS-1$
            if (_messageProperties.get("SEVERITY") != null) { //$NON-NLS-1$
                return _messageProperties.get("SEVERITY"); //$NON-NLS-1$
            }
        }

        // all other properties
        if (_messageProperties.containsKey(property)) {
            String s = _messageProperties.get(property);
            if (s != null) {
                return s;
            } else {
                return ""; //$NON-NLS-1$
            }
        } else {
            return ""; //$NON-NLS-1$
        }
    }

   public int getSeverityNumber() {
        return SeverityMapping.getSeverityNumber(_messageProperties.get("SEVERITY"));
    }

 //TODO jhatje: implement new datatypes

//    @Override
	public String getName() {
        return this.getProperty("NAME"); //$NON-NLS-1$
    }

//    @Override
	public String getTypeId() {
		return null;
//        return TYPE_ID;
    }


    public HashMap<String, String> getHashMap() {
        return _messageProperties;
    }


    /**
     * @return deep copy of the JMSMessage.
     */
    public BasicMessage copy(BasicMessage newMessage) {
        Set<String> properties = _messageProperties.keySet();
        for (String entry : properties) {
            String value = _messageProperties.get(entry);
            newMessage._messageProperties.put(entry, value);
        }
        return newMessage;
    }

}
