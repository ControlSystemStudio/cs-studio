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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
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

    volatile private String brokerURL;
    volatile private String clientID;
    volatile private String topicStr;

    //Random integer in case
    final static Integer randInt = ThreadLocalRandom.current().nextInt(0, 1000000 + 1);

    final protected static String SEPARATOR = " ";

    //volatile private String userName, passWord;

    private void analyzeName(final String name)
    {
        final int sep = name.indexOf(SEPARATOR);
        if (sep > 0)
        {
            brokerURL = name.substring(0, sep);
            topicStr = name.substring(sep+SEPARATOR.length());
        }
        else
        {
            brokerURL=name;
            topicStr = "mqttpv";
        }
    }

    protected MQTT_PV(final String name, final String base_name) throws Exception
    {
        super(name);
        analyzeName(base_name);

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

        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        //connOpt.setUserName(userName);
        //connOpt.setPassword(passWord.toCharArray());

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


    @Override
    public void write(final Object new_value) throws Exception
    {
        if (new_value == null)
            throw new Exception(getName() + " got null");

        if (!myClient.isConnected())
            throw new Exception(getName() + " not connected to " + brokerURL);

        MqttTopic topic = myClient.getTopic(topicStr);

        String pubMsg = "The PV is writing: " + new_value;
        int pubQoS = 0;
        MqttMessage message = new MqttMessage(pubMsg.getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);

        // Publish the message
        System.out.println("Publishing to topic \"" + topic + "\" qos " + pubQoS);
        MqttDeliveryToken token = null;
        try {
            // publish message to broker
            token = topic.publish(message);
            // Wait until the message has been delivered to the broker
            token.waitForCompletion();
            Thread.sleep(100);

            final VType value = ValueFactory.newVDouble(0.0);
            notifyListenersOfValue(value);

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
    public void messageArrived(String arg0, MqttMessage arg1) throws Exception
    {
        System.out.println("Message arrived: " + arg0 + " : " + arg1.toString());
    }
}
