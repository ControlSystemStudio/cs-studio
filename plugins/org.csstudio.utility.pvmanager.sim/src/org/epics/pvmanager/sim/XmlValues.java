/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author carcassi
 */
@XmlRootElement(name="values")
class XmlValues {

    @XmlElements({
        @XmlElement(name = "vDouble", type = XmlVDouble.class)
    })
    protected List<ReplayValue> value;

    @XmlAttribute
    private boolean adjustTime;

    public List<ReplayValue> getValues() {
        if (value == null) {
            value = new ArrayList<ReplayValue>();
        }
        return this.value;
    }

    public boolean isAdjustTime() {
        return adjustTime;
    }

    
}
