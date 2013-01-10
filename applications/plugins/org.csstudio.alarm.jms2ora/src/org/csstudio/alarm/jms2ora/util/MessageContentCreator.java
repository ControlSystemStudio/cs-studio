
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

import org.csstudio.alarm.jms2ora.Jms2OraActivator;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.csstudio.alarm.jms2ora.service.IMetaDataReader;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
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

    /** Class that collects statistic informations. Query it via XMPP. */
    private final StatisticCollector collector;

    /** Service for reading the meta data of the database tables */
    private IMetaDataReader metaDataService;

    /** Filter to avoid message storms */
    private final MessageFilter messageFilter;

    /** Number of bytes that can be stored in the column value of the table MESSAGE_CONTENT  */
    private int valueLength;

    /** Flag that indicates if empty property values should be stored */
    private final boolean storeEmptyValues;

    private final String formatStd = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}";
    private final String formatTwoDigits = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{2}";
    private final String formatOneDigit = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1}";

    public MessageContentCreator(StatisticCollector coll) {
        
        collector = coll;
        
        final IPreferencesService prefs = Platform.getPreferencesService();

        try {
            metaDataService = Jms2OraActivator.getDefault().getMetaDataReaderService();
        } catch (final OsgiServiceUnavailableException mace) {
            LOG.error("[*** MessageArchiveConnectionException ***]: {}", mace.getMessage());
        }

        readMessageProperties();

        valueLength = metaDataService.getValueLength();
        if(valueLength == -1) {
            valueLength = prefs.getInt(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.DEFAULT_VALUE_PRECISION, 300, null);
            LOG.warn("Cannot read the precision of the table column 'value'. Assume " + valueLength + " bytes");
        }

        initDiscardTypes();
        initDiscardNames();

        messageFilter = MessageFilter.getInstance();

        final String temp = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.STORE_EMPTY_VALUES,
                                      "false", null);
        storeEmptyValues = Boolean.parseBoolean(temp);
        LOG.info("Empty values will be stored: " + storeEmptyValues);
    }

    private void initDiscardTypes() {
        final IPreferencesService prefs = Platform.getPreferencesService();

        String temp = null;
        discardTypes = new Vector<String>();

        final String[] list = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.DISCARD_TYPES, "", null).split(",");
        if(list != null) {
            for(final String s : list) {
                temp = s.trim().toLowerCase();
                if(temp.length() > 0) {
                    discardTypes.add(temp);
                }
            }
        }
    }

    private void initDiscardNames() {

        final IPreferencesService prefs = Platform.getPreferencesService();

        String temp = null;
        discardNames = new Vector<String>();

        final String[] list = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.DISCARD_NAMES, "", null).split(",");
        if(list != null) {

            for(final String s : list) {

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
        return !msgProperty.isEmpty();
    }

    public synchronized void stopWorking() {
        messageFilter.stopWorking();
    }

    /**
     *
     * @param rawMsg
     * @return Vector object that contains all messages that have to be stored.
     */
    public final synchronized Vector<ArchiveMessage> convertRawMessages(final Vector<RawMessage> rawMsg) {

        final Vector<ArchiveMessage> result = new Vector<ArchiveMessage>();

        for (final RawMessage m : rawMsg) {
            final ArchiveMessage am = this.convertRawMessage(m);
            if(am.discard() || !am.hasContent()) {
                LOG.info("Message discarded or does not have any content: {}", am.toString());
            } else {
                result.add(am);
            }
        }

        return result;
    }

    public final synchronized ArchiveMessage convertRawMessage(final RawMessage rawMsg) {

        ArchiveMessage msgContent = null;
        String type = null;
        String propName = null;
        String et = null;
        String temp = null;
        boolean reload = false;
        boolean wrongFormat = false;

        // Create a new MessageContent object for the content of the message
        msgContent = new ArchiveMessage();

        if(rawMsg == null) {
            return msgContent;
        }

        // ACHTUNG: Die Message ist fuer den Client READ-ONLY!!!
        //          Jeder Versuch in die Message zu schreiben
        //          loest eine Exception aus.

        // First get the message type
        // Does the message contain the key TYPE?
        if(rawMsg.itemExists("TYPE")) {
            // Get the value of the item TYPE
            type = rawMsg.getValue("TYPE").toLowerCase();
        } else {
            // The message does not contain the item TYPE. We set it to UNKNOWN
            type = "unknown";
        }

        LOG.info("Message type: " + type);

        // Discard messages with the type 'simulator'
        if(!discardTypes.isEmpty()) {

            if(discardTypes.contains(type)) {

                msgContent.setDiscard(true);

                // Return an object without content
                // Call hasContent() to check whether or not content is available
                return msgContent;
            }
        }

        // Get the property 'NAME'
        // Does the message contain the key TYPE?
        if(rawMsg.itemExists("NAME")) {
            // Get the value of the item TYPE
            propName = rawMsg.getValue("NAME");
        } else {
            // The message does not contain the item NAME.
            propName = "";
        }

        // Discard messages that contains the names of the defined list
        if(!discardNames.isEmpty()) {

            if(discardNames.contains(propName)) {

                msgContent.setDiscard(true);
                collector.incrementDiscardedMessages();
                // Return an object without content
                // Call hasContent() to check whether or not content is available
                return msgContent;
            }
        }

        // Now get the event time
        if(rawMsg.itemExists("EVENTTIME")) {

            // Yes. Get it
            et = rawMsg.getValue("EVENTTIME");

            // Check the date format
            temp = checkDateString(et);

            // If there is something wrong with the format...
            if(temp == null) {

                LOG.warn("Property EVENTTIME contains invalid format: " + et);
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

        // Copy the type id and the event time
        msgContent.put(msgProperty.get("TYPE"), "TYPE", type);
        msgContent.put(msgProperty.get("EVENTTIME"), "EVENTTIME", et);
        msgContent.put(msgProperty.get("NAME"), "NAME", propName);

        final Enumeration<String> lst = rawMsg.getMapNames();

        // Copy the content of the message into the MessageContent object
        if (lst != null) {
            while (lst.hasMoreElements()) {
                final String name = lst.nextElement();

                // Get the value(String) and check its length
                temp = rawMsg.getValue(name);

                if(temp.length() == 0 && !storeEmptyValues) {
                    continue;
                }

                if(temp.length() > valueLength) {
                    temp = temp.substring(0, valueLength - 3) + "{*}";
                }

                // Do not copy the TYPE, EVENTTIME and NAME properties
                if(name.compareTo("TYPE") != 0
                        && name.compareTo("EVENTTIME") != 0
                        && name.compareTo("NAME") != 0) {

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
                LOG.warn(msgContent.toPrintableString());
                wrongFormat = false;
            }
        }

        if(messageFilter.shouldBeBlocked(msgContent)) {
            LOG.info("Block it!");
            msgContent.deleteContent();
            collector.incrementFilteredMessages();
        } else {
            LOG.info("Process it!");
        }

        return msgContent;
    }

    private void prepareAndSetUnknownProperty(final ArchiveMessage msgContent, final String name, final String value) {

        String temp = "[" + name + "] [" + value + "]";
        if(temp.length() > valueLength) {
            temp = temp.substring(0, valueLength - 4) + "{*}]";
        }

        msgContent.addUnknownProperty(temp);
        msgContent.setUnknownTableId(msgProperty.get("UNKNOWN"));
    }

    public final String getDateAndTimeString() {
        return getDateAndTimeString("[yyyy-MM-dd HH:mm:ss] ");
    }

    public final String getDateAndTimeString(final String frm) {

        final SimpleDateFormat    sdf = new SimpleDateFormat(frm);
        final GregorianCalendar   cal = new GregorianCalendar();

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
    public final String convertDateString(final String dateString, final String sourceFormat, final String destinationFormat)
    throws ParseException {

        final SimpleDateFormat ssdf = new SimpleDateFormat(sourceFormat);
        final SimpleDateFormat dsdf = new SimpleDateFormat(destinationFormat);
        String ds = null;

        try {
            final Date date = ssdf.parse(dateString);
            ds = dsdf.format(date);
        } catch(final ParseException pe) {
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
    public final String checkDateString(final String dateString) {

        String r = null;

        if(dateString.matches(formatStd)) {
            r = dateString;
        } else if(dateString.matches(formatTwoDigits)) {

            try {
                r = convertDateString(dateString, "yyyy-MM-dd HH:mm:ss.SS", "yyyy-MM-dd HH:mm:ss.SSS");
            } catch(final ParseException pe) {
                // Can be ignored
            }
        } else if(dateString.matches(formatOneDigit)) {

            try {
                r = convertDateString(dateString, "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.SSS");
            } catch(final ParseException pe) {
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

        // Delete old hash table, if there are any
        if(msgProperty != null) {
            msgProperty.clear();
            msgProperty = null;
        }

        msgProperty = new Hashtable<String, Long>();

        msgProperty = metaDataService.getMsgPropertyTypeContent();
        if(msgProperty.isEmpty()) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }
}
