/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Persistence handler for the property type <code>sds.map</code>.
 *
 * @author Kai Meyer
 *
 */
public final class StringMapPropertyPersistenceHandler extends
        AbstractPropertyPersistenceHandler {

    /**
     * XML tag name <code>map</code>.
     */
    public static final String XML_ELEMENT_MAP = "map"; //$NON-NLS-1$

    /**
     * XML tag name <code>mapEntry</code>.
     */
    public static final String XML_ELEMENT_MAP_ENTRY = "mapEntry"; //$NON-NLS-1$

    /**
     * XML attribute name <code>name</code>.
     */
    public static final String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

    /**
     * XML attribute name <code>value</code>.
     */
    public static final String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void writeProperty(final Element domElement,
            final Object propertyValue) {
        Map<String, String> aliases = (Map<String, String>) propertyValue;
        List<String> aliasKeys = new ArrayList<String>(aliases.keySet());
        Collections.sort(aliasKeys);

        Element mapElement = new Element(XML_ELEMENT_MAP);
        for (String key : aliasKeys) {
            Element mapEntryElement = new Element(XML_ELEMENT_MAP_ENTRY);
            mapEntryElement.setAttribute(XML_ATTRIBUTE_NAME, key); //$NON-NLS-1$
            mapEntryElement.setAttribute(XML_ATTRIBUTE_VALUE, aliases.get(key)); //$NON-NLS-1$
            mapElement.addContent(mapEntryElement);
        }

        domElement.addContent(mapElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readProperty(final Element domElement) {
        Element mapElement = domElement.getChild(XML_ELEMENT_MAP);

        Map<String, String> result = new HashMap<String, String>();

        for (Object o : mapElement.getChildren(XML_ELEMENT_MAP_ENTRY)) {
            Element mapEntryElement = (Element) o;

            String name = mapEntryElement.getAttributeValue(XML_ATTRIBUTE_NAME);
            String value = mapEntryElement.getAttributeValue(XML_ATTRIBUTE_VALUE);

            result.put(name, value);
        }

        return result;
    }
}
