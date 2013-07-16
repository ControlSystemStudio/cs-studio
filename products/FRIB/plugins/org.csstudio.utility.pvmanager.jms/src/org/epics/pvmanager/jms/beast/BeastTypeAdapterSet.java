/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms.beast;

import org.epics.pvmanager.jms.*;
import java.util.Collection;
import org.epics.pvmanager.DataSourceTypeAdapterSet;

/**
 *
 * @author carcassi
 */
public interface BeastTypeAdapterSet extends DataSourceTypeAdapterSet {

    @Override
    Collection<BeastTypeAdapter> getAdapters();
}
