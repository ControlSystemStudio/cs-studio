/*******************************************************************************
 * Copyright (c) 2014-2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.mqtt;

import java.util.List;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/** MQTT Process Variable
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class MQTT_PV extends PV
{
    static final MQTT_PVConn conx = new MQTT_PVConn();

    volatile private String topicStr;

    private Class<? extends VType> type;


    protected MQTT_PV(final String name, final String base_name) throws Exception
    {
        super(name);
        final String initial_value = parseTopicValue(base_name);

        if (initial_value == null)
        {
            notifyListenersOfDisconnect();
        }
        else
        {
            write(initial_value);
        }

        conx.subscribeTopic(topicStr, this);

    }

    private String parseTopicValue(final String base_name) throws Exception
    {
        final String[] ntv = parseName(base_name);
        topicStr = ntv[0];

        if (ntv[1] != null) {
            //topicStr += ntv[1];
            type = parseType(ntv[1]);
        }

        if (ntv[2] == null)
        {
            if (ntv[1] == null)
                type = VDouble.class;
        }
        else
        {
            final List<String> initial_value_items = VTypeToFromString.splitStringList(ntv[2]);
            if (ntv[1] == null)
                type = VTypeToFromString.determineValueType(initial_value_items);
        }

        return ntv[2];
    }

    /** Parse PV name
     *  @param base_name "name", "name(value)" or "name&lt;type>(value)"
     *  @return Name, type-or-null, value-or-null
     *  @throws Exception on error
     */
    public static String[] parseName(final String base_name) throws Exception
    {
        // Could use regular expression, but this allows more specific error messages
        String name=null, type=null, value=null;

        // Locate type
        int sep = base_name.indexOf('<');
        if (sep >= 0)
        {
            final int end = base_name.indexOf('>', sep+1);
            if (end <= sep)
                throw new Exception("Missing '>' to define type in " + base_name);
            name = base_name.substring(0, sep);
            type = base_name.substring(sep+1, end);
        }

        // Locate value
        sep = base_name.indexOf('(');
        if (sep > 0)
        {
            final int end = base_name.lastIndexOf(')');
            if (end <= sep)
                throw new Exception("Missing ')' of initial value in " + base_name);
            value = base_name.substring(sep+1, end);
            if (name == null)
                name = base_name.substring(0, sep);
        }

        if (name == null)
            name = base_name.trim();

        return new String[] { name, type, value };
    }

    private Class<? extends VType> parseType(final String type) throws Exception
    {   // Lenient check, ignore case and allow partial match
        final String lower = type.toLowerCase();
        if (lower.contains("doublearray"))
            return VDoubleArray.class;
        if (lower.contains("double"))
            return VDouble.class;
        if (lower.contains("stringarray"))
            return VStringArray.class;
        if (lower.contains("string"))
            return VString.class;
        if (lower.contains("enum"))
            return VEnum.class;
        if (lower.contains("long"))
            return VLong.class;
        if (lower.contains("table"))
            return VTable.class;
        throw new Exception("MQTT PV cannot handle type '" + type + "'");
    }



    /**
     * This is QoS 0 with retention (fire and forget)
     * @see org.csstudio.vtype.pv.PV#write(java.lang.Object)
     */
    @Override
    public void write(final Object new_value) throws Exception
    {
        if (new_value == null)
            throw new Exception(getName() + " got null");

        final String pubMsg;
        try
        {
            final VType value = VTypeToFromString.convert(new_value, type, read());
            pubMsg = VTypeToFromString.ToString(value);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to adapt object '" + new_value + "' to " + getName(), ex);
        }


        try {
            conx.publishTopic(topicStr, pubMsg, 0, true);
        } catch (Exception ex) {
            throw new Exception("Failed to write '" + new_value + "' to " + getName(), ex);
        }

    }

    @Override
    protected void close()
    {
        try
        {
            conx.unsubscribeTopic(topicStr, this);
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Failed to unsubscribe PV from topic " + topicStr);
            e.printStackTrace();
        }
    }


    /**
     * Called when a message arrives from a subscribed topic
     */
    public void messageArrived(String topic, MqttMessage msg) throws Exception
    {
        final String new_value = msg.toString();
        System.out.println("MQTT_PV Message arrived: " + topic + " : " + msg.toString());

        if (!topic.equals(topicStr))
        {
            logger.log(Level.SEVERE, "Got message with topic " + topic + " != " + topicStr);
            throw new Exception(getName() + " topic mismatch");
        }

        try
        {
            final VType value = VTypeToFromString.convert(new_value, type, read());
            notifyListenersOfValue(value);
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Could not parse message: '" + new_value + "' to " + getName());
            ex.printStackTrace();
            //throw new Exception("Failed to parse message", ex);
        }
    }



}
