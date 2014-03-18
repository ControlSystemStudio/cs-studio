/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * XML parse for the Replay function.
 *
 * @author carcassi
 */
class ReplayParser {

    private ReplayParser() {
        // Avoid construction
    }

    /**
     * Reads the XML file located at the given uri.
     *
     * @param uri local url for a local file, or absolute uri for any other protocol
     * @return the parsed file
     */
    static XmlValues parse(URI uri) {
        // If relative, resolve it in the current directory
        if (!uri.isAbsolute()) {
            File current = new File(".");
            uri = current.toURI().resolve(uri);
        }
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(XmlValues.class);
            Unmarshaller reader = jaxbCtx.createUnmarshaller();
            XmlValues values = (XmlValues) reader.unmarshal(uri.toURL());

            // Adjust all values by using the previous as default
            ReplayValue previousValue = null;
            for (ReplayValue newValue : values.getValues()) {
                if (previousValue == null) {
                    previousValue = newValue;
                } else {
                    newValue.updateNullValues(previousValue);
                }
            }
            return values;
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Can't access file", ex);
        } catch (JAXBException ex) {
            throw new RuntimeException("Can't parse file", ex);
        }
    }

}
