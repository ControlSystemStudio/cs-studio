/*******************************************************************************
 * Copyright (c) 2014-2016 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.mqtt;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.internal.Preferences;
import org.csstudio.vtype.pv.local.ValueHelper;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/** MQTT Process Variable
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class MQTT_PV extends PV implements MqttCallback
{
    MqttClient myClient;
    MqttConnectOptions connOpt;

    //TODO: broker connection fail: AlarmSeverity.UNDEFINED
    // client device never sent anything or sent LWT: AlarmSeverity.INVALID

    volatile private String brokerURL;
    volatile private String clientID;
    volatile private String topicStr;

    private Class<? extends VType> type;

    //Random integer in case
    final static Integer randInt = ThreadLocalRandom.current().nextInt(0, 1000000 + 1);

    //volatile private String userName, passWord;

    private void generateClientID()
    {
        try
        {
            NetworkInterface nwi = NetworkInterface.getByIndex(0);
            byte mac[] = nwi.getHardwareAddress();
            clientID = String.valueOf(mac) + String.valueOf(randInt);
        }
        catch(Exception e)
        {
            try {
                InetAddress address = InetAddress.getLocalHost();
                clientID = address.getCanonicalHostName() + String.valueOf(randInt);
            }
            catch(Exception e2)
            {
                clientID = String.valueOf(randInt);
            }
        }

        //System MAC address (or hostname) + random integer + object hash... hopefully unique?
        clientID += "-" + System.identityHashCode(this);
    }


    private void setOptions()
    {
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        //connOpt.setUserName(userName);
        //connOpt.setPassword(passWord.toCharArray());
        //TODO: Look up best practices for reconnect
        //connOpt.setAutomaticReconnect(true);
    }


    protected MQTT_PV(final String name, final String base_name) throws Exception
    {
        super(name);
        parseNameTypeValue(base_name);

        //TODO: Change to have single client connection for all PVs

        brokerURL = Preferences.getMQTTBroker();
        topicStr = base_name;

        generateClientID();
        setOptions();

        // Connect to Broker
        try {
            myClient = new MqttClient(brokerURL, clientID);
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            logger.log(Level.SEVERE, "Could not connect to MQTT broker " + brokerURL);
            e.printStackTrace();
        }

        try {
            int subQoS = 0;
            myClient.subscribe(topicStr, subQoS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseNameTypeValue(final String base_name) throws Exception
    {
        final String[] ntv = ValueHelper.parseName(base_name);
        final VType initial_value;

        if (ntv[1] != null)
            type = parseType(ntv[1]);

        if (ntv[2] == null)
        {
            if (ntv[1] == null)
                type = VDouble.class;

            initial_value = null;
        }
        else
        {
            final List<String> initial_value_items = ValueHelper.splitInitialItems(ntv[2]);
            if (ntv[1] == null)
                type = determineValueType(initial_value_items);

            initial_value = ValueHelper.getInitialValue(initial_value_items, type);
        }

        if (initial_value == null)
        {
            notifyListenersOfDisconnect();
        }
        else
        {
            notifyListenersOfValue(initial_value);
        }
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
        throw new Exception("Local PV cannot handle type '" + type + "'");
    }

    private Class<? extends VType> determineValueType(final List<String> items) throws Exception
    {
        if (ValueHelper.haveInitialStrings(items))
        {
            if (items.size() == 1)
                return VString.class;
            else
                return VStringArray.class;
        }
        else
        {
            if (items.size() == 1)
                return VDouble.class;
            else
                return VDoubleArray.class;
        }
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

        if (!myClient.isConnected())
            throw new Exception(getName() + " not connected to " + brokerURL);

        String pubMsg = new_value.toString();
        parseAndNotify(pubMsg);

        MqttTopic topic = myClient.getTopic(topicStr);
        int pubQoS = 0;
        MqttMessage message = new MqttMessage(pubMsg.getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);

        // Publish the message
        System.out.println("Publishing \"" + pubMsg + "\" to topic \"" + topic + "\" qos " + pubQoS);
        MqttDeliveryToken token = null;
        try {
            // publish message to broker
            token = topic.publish(message);
            // Wait until the message has been delivered to the broker
            token.waitForCompletion();
            Thread.sleep(100);
        } catch (Exception ex) {
            throw new Exception("Failed to write '" + new_value + "' to " + getName(), ex);
        }

    }

    @Override
    protected void close()
    {
        if (myClient.isConnected())
        {
            try {
                // wait to ensure subscribed messages are delivered
                Thread.sleep(100);
                myClient.disconnect();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to disconnect from MQTT broker " + brokerURL);
                e.printStackTrace();
            }
        }
    }


    /**
     * Called when connection to broker is lost
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
     */
    @Override
    public void connectionLost(Throwable arg0)
    {
        logger.log(Level.FINE, "Disconnected from MQTT broker " + brokerURL);

        // Connect to Broker
        // TODO: attempt reconnect repeatedly in background thread with timer backoff and eventual timeout
        try {
            myClient.connect(connOpt);
        } catch (MqttException e) {
            logger.log(Level.SEVERE, "Could not reconnect to MQTT broker " + brokerURL);
            e.printStackTrace();
        }
    }



    /**
     * Called when message at QoS 1 or 2 acknowledges arrival at broker (QoS 0 will never ack)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0)
    {
        // TODO Auto-generated method stub

    }


    /**
     * Called when a message arrives from a subscribed topic
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception
    {
        final String new_value = msg.toString();
        System.out.println("Message arrived: " + topic + " : " + msg.toString());

        if (!topic.equals(topicStr))
        {
            logger.log(Level.SEVERE, "Got message with topic " + topic + " != " + topicStr);
            throw new Exception(getName() + " topic mismatch");
        }

        parseAndNotify(new_value);
    }


    private void parseAndNotify(final String new_value) throws Exception
    {
        try
        {
            final VType value = ValueHelper.adapt(new_value, type, read());
            notifyListenersOfValue(value);
        }
        catch (Exception ex)
        {
            throw new Exception("Failed to parse message '" + new_value + "' to " + getName(), ex);
        }
    }
}
