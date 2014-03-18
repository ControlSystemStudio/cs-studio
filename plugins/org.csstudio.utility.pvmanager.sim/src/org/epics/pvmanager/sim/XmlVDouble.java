/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import javax.xml.bind.annotation.XmlAttribute;
import org.epics.vtype.VDouble;

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
