/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.vtype.json;

import javax.json.JsonObject;
import org.epics.vtype.VType;

/**
 * 
 * @author carcassi
 */
public class VTypeToJson {

    public static VType toVType(JsonObject json) {
        return VTypeToJsonV1.toVType(json);
    }
    
    public static JsonObject toJson(VType vType) {
        return VTypeToJsonV1.toJson(vType);
    }
}
