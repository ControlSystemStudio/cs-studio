/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import javax.xml.bind.annotation.XmlAttribute;
import org.epics.pvmanager.data.VDouble;

/**
 *
 * @author carcassi
 */
class XmlVDouble extends XmlVNumberMetaData implements VDouble {

    @XmlAttribute
    Double value;

    @Override
    public Double getValue() {
        return value;
    }

}
