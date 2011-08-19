
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
 *
 */

package org.csstudio.alarm.jms2ora.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.slf4j.Logger;
import org.csstudio.alarm.jms2ora.Jms2OraPlugin;
import org.csstudio.alarm.jms2ora.database.DatabaseLayer;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.LoggerFactory;

/**
 *  @author Markus Moeller
 *
 */
public class MessageContentCreator {
    
    /** the class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageContentCreator.class);

    /** Vector object contains names of message types that should be discarded */
    private Vector<String> discardTypes;
    
    /** Vector object contains names of message types that should be discarded */
    private Vector<String> discardNames;

    /** Hashtable with message properties. Key -> name, value -> database table id  */
    private Hashtable<String, Long> msgProperty;
        
    /** Object for database handling */
    private DatabaseLayer dbLayer;
    
    /** Filter to avoid message storms */
    private MessageFilter messageFilter;

    /** Number of bytes that can be stored in the column value of the table MESSAGE_CONTENT  */
    private int valueLength;
    
    /** Flag that indicates if empty property values should be stored */
    private boolean storeEmptyValues;

    private final String formatStd = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}";
    private final String formatTwoDigits = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{2}";
    private final String formatOneDigit = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1}";

    public MessageContentCreator(String dbUrl, String dbUser, String dbPassword) {
        
        IPreferencesService prefs = Platform.getPreferencesService();

        dbLayer = new DatabaseLayer(dbUrl, dbUser, dbPassword);
        
        readMessageProperties();

        valueLength = dbLayer.getMaxNumberofValueBytes();
        if(valueLength == 0) {
            valueLength = prefs.getInt(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DEFAULT_VALUE_PRECISION, 300, null);
            LOG.warn("Cannot read the precision of the table column 'value'. Assume " + valueLength + " bytes");
        }
        
        initDiscardTypes();
        initDiscardNames();
        
        messageFilter = MessageFilter.getInstance();
        
        String temp = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.STORE_EMPTY_VALUES,
                                      "false", null);
        storeEmptyValues = Boolean.parseBoolean(temp);
        LOG.info("Empty values will be stored: " + storeEmptyValues);
    }
    
    private void initDiscardTypes() {
        IPreferencesService prefs = Platform.getPreferencesService();

        String temp = null;
        discardTypes = new Vector<String>();
        
        String list[] = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DISCARD_TYPES, "", null).split(",");
        if(list != null) {
            
            for(String s : list) {
                
                temp = s.trim().toLowerCase();
                if(temp.length() > 0) {
                    discardTypes.add(temp);
                }
            }
        }
    }

    private void initDiscardNames() {
        
        IPreferencesService prefs = Platform.getPreferencesService();

        String temp = null;
        discardNames = new Vector<String>();
        
        String list[] = prefs.getString(Jms2OraPlugin.PLUGIN_ID, PreferenceConstants.DISCARD_NAMES, "", null).split(",");
        if(list != null) {
            
            for(String s : list) {
                
                temp = s.trim();
                if(temp.length() > 0) {
                    discardNames.add(temp);
                }
            }
        }
    }

    /**
     * Checks whether or not content is available.
     * 
     *  @return true, if content is available, otherwise false
     */
    public boolean arePropertiesAvailable() {
        return (!msgProperty.isEmpty());
    }
    
    public synchronized void stopWorking() {
        messageFilter.stopWorking();
    }
    
    public synchronized MessageContent convertMapMessage(MapMessage mmsg) {
        
        MessageContent msgContent = null;
        Enumeration<?> lst = null;
        String name = null;
        String type = null;
        String propName = null;
        String et = null;
        String temp = null;
        boolean reload = false;
        boolean wrongFormat = false;

        LOG.debug("Enter MessageContentCreator.convertMapMessage()");
        
        // Create a new MessageContent object for the content of the message
        msgContent = new MessageContent();

        if(mmsg == null) {
            
            LOG.debug("Leaving MessageContentCreator.convertMapMessage()");
            return msgContent;
        }
        
        // ACHTUNG: Die Message ist für den Client READ-ONLY!!! Jeder Versuch in die Message zu schreiben
        //          löst eine Exception aus.

        // First get the message type
        try {
            
            // Does the message contain the key TYPE?
            if(mmsg.itemExists("TYPE")) {
                // Get the value of the item TYPE
                type = mmsg.getString("TYPE").toLowerCase();                
            } else {
                // The message does not contain the item TYPE. We set it to UNKNOWN
                type = "unknown";                
            }
        } catch(JMSException jmse) {
            type = "unknown";
        }
        
        LOG.debug("Message type: " + type);
        
        // Discard messages with the type 'simulator'
        if(!discardTypes.isEmpty()) {
            
            if(discardTypes.contains(type)) {
                
                msgContent.setDiscard(true);
                
                LOG.debug("Leaving MessageContentCreator.convertMapMessage()");

                // Return an object without content
                // Call hasContent() to check whether or not content is available
                return msgContent;
            }
        }
        
        // Get the property 'NAME'
        try {
            
            // Does the message contain the key TYPE?
            if(mmsg.itemExists("NAME")) {
                // Get the value of the item TYPE
                propName = mmsg.getString("NAME");                
            } else {
                // The message does not contain the item NAME.
                propName = "";                
            }
        } catch(JMSException jmse) {
            propName = "";
        }

        LOG.debug("Property NAME: " + propName);

        // Discard messages that contains the names of the defined list
        if(!discardNames.isEmpty()) {
            
            if(discardNames.contains(propName)) {
                
                msgContent.setDiscard(true);
                
                LOG.debug("Leaving MessageContentCreator.convertMapMessage()");

                // Return an object without content
                // Call hasContent() to check whether or not content is available
                return msgContent;
            }
        }

        // Now get the event time
        try {
            
            if(mmsg.itemExists("EVENTTIME")) {
                
                // Yes. Get it
                et = mmsg.getString("EVENTTIME");

                // Check the date format
                temp = checkDateString(et);
                
                // If there is something wrong with the format...
                if(temp == null) {
                    
                    LOG.info("Property EVENTTIME contains invalid format: " + et);
                    wrongFormat = true;
                    
                    // ... create a new date string
                    et = getDateAndTimeString("yyyy-MM-dd HH:mm:ss.SSS");
                } else {
                    // ... otherwise 'temp' contains a valid date string
                    et = temp;
                }
            } else {
                // Get the current date and time
                // Format: 2006.07.26 12:49:12.345
                et = getDateAndTimeString("yyyy-MM-dd HH:mm:ss.SSS");
            }
        } catch(JMSException jmse) {
            et = getDateAndTimeString("yyyy-MM-dd HH:mm:ss.SSS");
        }
        
        // Copy the type id and the event time
        msgContent.put(msgProperty.get("TYPE"), "TYPE", type);        
        msgContent.put(msgProperty.get("EVENTTIME"), "EVENTTIME", et);
        msgContent.put(msgProperty.get("NAME"), "NAME", propName);
        
        try {
            lst = mmsg.getMapNames();
        } catch(JMSException jmse) {
            // Put the exception message into the message content
            msgContent.put(msgProperty.get("TEXT"), "TEXT", "[JMSException] " + jmse.getMessage());
        }
        
        // Copy the content of the message into the MessageContent object
        if(lst != null) {
            while(lst.hasMoreElements())
            {
                name = (String)lst.nextElement();
                
                // Get the value(String) and check its length
                try {
                    temp = mmsg.getString(name);
                    //temp = temp.trim();
                } catch(JMSException jmse) {
                    temp = "[JMSException] Cannot read the element: " + jmse.getMessage();
                }
                
                if((temp.length() == 0) && storeEmptyValues == false) {
                    continue;
                }

                if(temp.length() > valueLength) {
                    temp = temp.substring(0, valueLength - 3) + "{*}";
                }
                
                // Do not copy the TYPE, EVENTTIME and NAME properties
                if((name.compareTo("TYPE") != 0) 
                        && (name.compareTo("EVENTTIME") != 0)
                        && (name.compareTo("NAME") != 0)) {
                    
                    // If we know the property and the value is NOT empty
                    if(msgProperty.containsKey(name)) {
                        // Get the ID of the property and store it into the hash table
                        msgContent.put(msgProperty.get(name), name, temp);
                    } else {                        
                        
                        // Reload the tables if they are not reloaded
                        if(!reload) {
                            
                            if(readMessageProperties()) {
                                
                                reload = true;
                                
                                // Check again
                                if(msgProperty.containsKey(name)) {
                                    msgContent.put(msgProperty.get(name), name, temp);
                                } else {
                                    // ...so we have to store them seperately
                                    prepareAndSetUnknownProperty(msgContent, name, temp);
                                }
                            } else {
                                LOG.error("Cannot read the message properties. Use the old set of properties.");
                                prepareAndSetUnknownProperty(msgContent, name, temp);
                            }
                        } else {
                            prepareAndSetUnknownProperty(msgContent, name, temp);
                        }
                    }                    
                }
            }
            
            if(wrongFormat) {
                LOG.info(msgContent.toPrintableString());
                wrongFormat = false;
            }
        }
        
        if(messageFilter.shouldBeBlocked(msgContent)) {
            LOG.debug("Block it!");
            msgContent.deleteContent();
        } else {
            LOG.debug("Process it!");
        }

        LOG.debug("Leaving MessageContentCreator.convertMapMessage()");

        return msgContent;
    }
    
    private void prepareAndSetUnknownProperty(MessageContent msgContent, String name, String value) {
        
        String temp = "[" + name + "] [" + value + "]";
        if(temp.length() > valueLength) {
            temp = temp.substring(0, valueLength - 4) + "{*}]";
        }
        
        msgContent.addUnknownProperty(temp);
        msgContent.setUnknownTableId(msgProperty.get("UNKNOWN"));
    }
    
    public String getDateAndTimeString() {
        return getDateAndTimeString("[yyyy-MM-dd HH:mm:ss] ");
    }

    public String getDateAndTimeString(String frm) {
        
        SimpleDateFormat    sdf = new SimpleDateFormat(frm);
        GregorianCalendar   cal = new GregorianCalendar();
        
        return sdf.format(cal.getTime());
    }

    /**
     * Converts the time and date string using only one or two digits for the mili seconds
     * to a string using 3 digits for the mili seconds with leading zeros
     * 
     * @param dateString The date string that has to be converted
     * @param sourceFormat The format of the converted string
     * @param destinationFormat The format of the result string
     * @throws ParseException If the date string does not match the source format
     * @return The converted date and time string
     */
    public String convertDateString(String dateString, String sourceFormat, String destinationFormat)
    throws ParseException {
        
        SimpleDateFormat ssdf = new SimpleDateFormat(sourceFormat);
        SimpleDateFormat dsdf = new SimpleDateFormat(destinationFormat);
        String ds = null;
        
        try {
            Date date = ssdf.parse(dateString);
            ds = dsdf.format(date);
        } catch(ParseException pe) {
            throw new ParseException("String [" + dateString + "] is invalid: " + pe.getMessage(), pe.getErrorOffset());
        }
        
        return ds;
    }
    
    /**
     * Checks wether or not the date and time string uses the standard format yyyy-MM-dd HH:mm:ss.SSS
     * 
     * @param dateString
     * @return The date and time string that uses the standard format.
     */
    public String checkDateString(String dateString) {
        
        String r = null;
        
        if(dateString.matches(formatStd)) {
            r = dateString;
        } else if(dateString.matches(formatTwoDigits)) {
            
            try {
                r = convertDateString(dateString, "yyyy-MM-dd HH:mm:ss.SS", "yyyy-MM-dd HH:mm:ss.SSS");
            } catch(ParseException pe) {
                // Can be ignored
            }
        } else if(dateString.matches(formatOneDigit)) {
            
            try {
                r = convertDateString(dateString, "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SSS");
            } catch(ParseException pe) {
             // Can be ignored
            }            
        }
        
        return r;
    }
    
    /**
     *  The method fills a hash table with the information from
     *  the database table MSG_PROPERTY_TYPE.
     *  
     *  @return true/false
     */
    
    private boolean readMessageProperties() {
        
        boolean result = false;
                
        LOG.debug("Entering MessageContentCreator.readMessageProperties(): Reading message properties.");
        
        // Delete old hash table, if there are any
        if(msgProperty != null) {
            msgProperty.clear();
            msgProperty = null;
        }

        msgProperty = new Hashtable<String, Long>();

        msgProperty = dbLayer.getMessageProperties();
        if(msgProperty.isEmpty()) {
            result = false;
        } else {
            result = true;
        }
        
        LOG.debug("Leaving MessageContentCreator.readMessageProperties()");

        return result;
    }    
}
