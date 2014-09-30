/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.epics.pvmanager.file.FileFormat;
import org.epics.vtype.VType;
import org.epics.vtype.json.VTypeToJson;


/**
 * JSON serialization of VTypes (.jvtype).
 * 
 * @author Kunal Shroff
 *
 */
public class JVTypeFileFormat implements FileFormat {
    
    @Override
    public Object readValue(InputStream in) {
        try (JsonReader reader = Json.createReader(in)) {
            return VTypeToJson.toVType(reader.readObject());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void writeValue(Object value, OutputStream out) {
        try (JsonWriter writer = Json.createWriter(out)) {
            writer.writeObject(VTypeToJson.toJson((VType) value));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public boolean isWriteSupported() {
	return true;
    }

    @Override
    public Collection<String> getFileExtensions() {
        return Arrays.asList("jvtype");
    }
    
}
