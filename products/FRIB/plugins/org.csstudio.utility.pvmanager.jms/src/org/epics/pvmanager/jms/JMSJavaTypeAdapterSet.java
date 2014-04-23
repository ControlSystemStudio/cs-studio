/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import org.epics.pvmanager.jms.adapters.BytesMessageToListByte;
import org.epics.pvmanager.jms.adapters.MapMessageToMap;
import org.epics.pvmanager.jms.adapters.StreamMessageToDouble;
import org.epics.pvmanager.jms.adapters.StreamMessageToInteger;
import org.epics.pvmanager.jms.adapters.TextMessageToString;
import org.epics.util.array.ListByte;



/**
 *
 * @author carcassi
 */
public class JMSJavaTypeAdapterSet implements JMSTypeAdapterSet {

    @Override
    public Set<JMSTypeAdapter> getAdapters() {
        return converters;
    }
    //  -> Map
    final static JMSTypeAdapter ToMap = new JMSTypeAdapter(Map.class, MapMessage.class) {
        @Override
        public Map createValue(Message message, boolean disconnected) {
            MapMessageToMap mapMessageToMap = null;
            try {
                mapMessageToMap = new MapMessageToMap(((MapMessage)message), disconnected);
            } catch (JMSException ex) {
                Logger.getLogger(JMSJavaTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
                return mapMessageToMap.getValue();
        }
    };
    //  -> Double
    final static JMSTypeAdapter ToDouble = new JMSTypeAdapter(Double.class, StreamMessage.class) {
        @Override
        public Double createValue(Message message, boolean disconnected) {
            StreamMessageToDouble streamMessageToDouble = null;
            try {
                streamMessageToDouble = new StreamMessageToDouble(((StreamMessage)message), disconnected);
            } catch (JMSException ex) {
                Logger.getLogger(JMSJavaTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
            return streamMessageToDouble.getValue();
        }
    };
    //  -> Integer
    final static JMSTypeAdapter ToInteger = new JMSTypeAdapter(Integer.class, StreamMessage.class) {
        @Override
        public Integer createValue(final Message message, boolean disconnected) {
            StreamMessageToInteger streamMessageToInteger = null;
            try {
                streamMessageToInteger = new StreamMessageToInteger(((StreamMessage)message), disconnected);
            } catch (JMSException ex) {
                Logger.getLogger(JMSJavaTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
            return streamMessageToInteger.getValue() ;
        }
    };
    //  -> String
    final static JMSTypeAdapter ToString = new JMSTypeAdapter(String.class, TextMessage.class) {
        @Override
        public String createValue(final Message message, boolean disconnected) {
            TextMessageToString textMessageToString = null;
            try {
                textMessageToString = new TextMessageToString(((TextMessage)message), disconnected);
            } catch (JMSException ex) {
                Logger.getLogger(JMSJavaTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
            return textMessageToString.getValue();
        }
    };

    //  -> ArrayByte
    final static JMSTypeAdapter ToArrayByte = new JMSTypeAdapter(ListByte.class, BytesMessage.class) {
        @Override
        public ListByte createValue(final Message message, boolean disconnected) {
            BytesMessageToListByte bytesMessageToByteArray = null;
            try {
                bytesMessageToByteArray = new BytesMessageToListByte(((BytesMessage)message), disconnected);
            } catch (JMSException ex) {
                Logger.getLogger(JMSJavaTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
            return bytesMessageToByteArray.getData();
        }
    };

    private static final Set<JMSTypeAdapter> converters;

    static {
        Set<JMSTypeAdapter> newFactories = new HashSet<JMSTypeAdapter>();

        newFactories.add(ToMap);
        // Add all SCALARs
        newFactories.add(ToDouble);
        newFactories.add(ToInteger);
        newFactories.add(ToString);


        // Add all ARRAYs

        newFactories.add(ToArrayByte);
        //newFactories.add(ToVArrayEnum);

        converters = Collections.unmodifiableSet(newFactories);
    }
}
