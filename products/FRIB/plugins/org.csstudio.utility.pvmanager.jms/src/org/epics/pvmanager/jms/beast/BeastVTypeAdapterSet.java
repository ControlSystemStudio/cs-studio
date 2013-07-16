/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms.beast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.epics.vtype.VString;



/**
 *
 * @author carcassi
 */
public class BeastVTypeAdapterSet implements BeastTypeAdapterSet {

    @Override
    public Set<BeastTypeAdapter> getAdapters() {
        return converters;
    }
    //  -> String
    final static BeastTypeAdapter ToVString = new BeastTypeAdapter(VString.class, MapMessage.class) {
        @Override
        public VString createValue(Message message, boolean disconnected) {
            MapMessageToVString mapMessageToVString = null;
            try {
                mapMessageToVString = new MapMessageToVString(((MapMessage)message), disconnected);
            } catch (Exception ex) {
                Logger.getLogger(BeastVTypeAdapterSet.class.getName()).log(Level.SEVERE, null, ex);
            }
                return mapMessageToVString;
        }
    };
   

    private static final Set<BeastTypeAdapter> converters;

    static {
        Set<BeastTypeAdapter> newFactories = new HashSet<BeastTypeAdapter>();

        newFactories.add(ToVString);

        converters = Collections.unmodifiableSet(newFactories);
    }
}
