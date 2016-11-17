/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.mqtt;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.internal.Preferences;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/** Factory for creating {@link LocalPV}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MQTT_PVConn implements MqttCallback
{
    MqttClient myClient;
    MqttConnectOptions connOpt;

    final Map<String, Set<MQTT_PV>> subscribers = new ConcurrentHashMap<String, Set<MQTT_PV>>();

    volatile private String brokerURL;
    volatile private String clientID;

    boolean is_connected;
    final Object conn_lock = new Object();

    //Random integer in case
    final static Integer randInt = ThreadLocalRandom.current().nextInt(0, 1000000 + 1);

    //volatile private String userName, passWord;

    MQTT_PVConn()
    {
        connect();
    }

    /**
     * Called when a message arrives from a subscribed topic
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception
    {
        System.out.println("MQTT Connections Message arrived: " + topic + " : " + msg.toString());

        //Synchronize this so a pv can't unsubscribe and leave an invalid pointer in the map while we deliver message
        synchronized(subscribers)
        {
            if (subscribers.containsKey(topic))
            {
                for (MQTT_PV pv : subscribers.get(topic))
                {
                    pv.messageArrived(topic, msg);
                }
            }
        }

    }

    public void subscribeTopic (String topicStr, MQTT_PV pv) throws Exception
    {
        if ((!is_connected) && (!connect()))
        {
            PV.logger.log(Level.WARNING, "Could not subscribe to mqtt topic \"" + topicStr
                    + "\" due to no broker connection");
            throw new Exception("MQTT subscribe failed: no broker connection");
        }

        if (!subscribers.containsKey(topicStr))
        {
            synchronized(subscribers)
            {
                if (!subscribers.containsKey(topicStr))
                {
                    subscribers.put(topicStr, ConcurrentHashMap.newKeySet());
                    int subQoS = 0;
                    myClient.subscribe(topicStr, subQoS);
                }
            }
        }
        subscribers.get(topicStr).add(pv);
    }

    public void unsubscribeTopic (String topicStr, MQTT_PV pv) throws Exception
    {
        if ((!is_connected) && (!connect()))
        {
            PV.logger.log(Level.WARNING, "Could not unsubscribe to mqtt topic \"" + topicStr
                    + "\" due to no broker connection");
            throw new Exception("MQTT unsubscribe failed: no broker connection");
        }

        if (!subscribers.containsKey(topicStr))
        {
            PV.logger.log(Level.WARNING, "Could not unsubscribe to mqtt topic \"" + topicStr
                    + "\" due to no internal record of topic");
            throw new Exception("MQTT unsubscribe failed: no topic record");
        }

        subscribers.get(topicStr).remove(pv);

        if (subscribers.get(topicStr).size() == 0)
        {
            synchronized(subscribers)
            {
                if (subscribers.get(topicStr).size() == 0)
                {
                    subscribers.remove(topicStr);
                    myClient.unsubscribe(topicStr);
                }
                if (subscribers.isEmpty())
                {
                    disconnect();
                }
            }
        }

    }

    public void publishTopic(String topicStr, String pubMsg, int pubQoS, boolean retained) throws Exception
    {
        if ((!is_connected) && (!connect()))
        {
            PV.logger.log(Level.WARNING, "Could not publish to mqtt topic \"" + topicStr
                    + "\" due to no broker connection");
            throw new Exception("MQTT publish failed: no broker connection");
        }

        MqttTopic topic = myClient.getTopic(topicStr);
        MqttMessage message = new MqttMessage(pubMsg.getBytes());
        message.setQos(pubQoS);
        message.setRetained(retained);

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
            throw new Exception("Failed to publish message to broker", ex);
        }
    }

    private void disconnect()
    {
        if (is_connected) {
            synchronized(conn_lock)
            {
                if (is_connected)
                {
                    if (myClient.isConnected())
                    {
                        try {
                            // wait to ensure subscribed messages are delivered
                            Thread.sleep(100);
                            myClient.disconnect();
                            is_connected = false;
                        } catch (Exception e) {
                            PV.logger.log(Level.WARNING, "Failed to disconnect from MQTT broker " + brokerURL);
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        is_connected = false;
                    }
                }
            }
        }
    }

    private boolean connect()
    {
        if (!is_connected)
        {
            synchronized(conn_lock)
            {
                if (!is_connected)
                {
                    brokerURL = Preferences.getMQTTBroker();
                    generateClientID();
                    setOptions();

                    // Connect to Broker
                    try {
                        myClient = new MqttClient(brokerURL, clientID);
                        myClient.setCallback(this);
                        myClient.connect(connOpt);
                        is_connected = true;
                    } catch (MqttException e) {
                        PV.logger.log(Level.SEVERE, "Could not connect to MQTT broker " + brokerURL);
                        e.printStackTrace();
                    }
                }
            }
        }
        return  is_connected;
    }

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

    /**
     * Called when connection to broker is lost
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
     */
    @Override
    public void connectionLost(Throwable arg0)
    {
        PV.logger.log(Level.FINE, "Disconnected from MQTT broker " + brokerURL);

        // Connect to Broker
        // TODO: attempt reconnect repeatedly in background thread with timer backoff and eventual timeout
        try {
            myClient.connect(connOpt);
        } catch (MqttException e) {
            PV.logger.log(Level.SEVERE, "Could not reconnect to MQTT broker " + brokerURL);
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




}
