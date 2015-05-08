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

import org.jdom.Element;

/**
 * Persistence handler for the property type <code>sds.doublearray</code>.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class DoubleArrayPropertyPersistenceHandler extends
        AbstractPropertyPersistenceHandler {

    /**
     * XML tag name <code>double</code>.
     */
    public static final String XML_ELEMENT_DOUBLE = "double"; //$NON-NLS-1$

    /**
     * XML tag name <code>doubleArray</code>.
     */
    public static final String XML_ELEMENT_DOUBLE_ARRAY = "doubleArray"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeProperty(final Element domElement,
            final Object propertyValue) {
        double[] doubleArray = (double[]) propertyValue;

        Element arrayElement = new Element(XML_ELEMENT_DOUBLE_ARRAY);

        for (double d : doubleArray) {
            Element doubleElement = new Element(XML_ELEMENT_DOUBLE);
            doubleElement
                    .setAttribute(XmlConstants.XML_ATTRIBUTE_VALUE, "" + d); //$NON-NLS-1$
            arrayElement.addContent(doubleElement);
        }

        domElement.addContent(arrayElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readProperty(final Element domElement) {
        ArrayList<Double> values = new ArrayList<Double>();

        Element doubleArrayElement = domElement
                .getChild(XML_ELEMENT_DOUBLE_ARRAY);

        for (Object o : doubleArrayElement.getChildren(XML_ELEMENT_DOUBLE)) {
            Element valueElement = (Element) o;
            values.add(Double.parseDouble(valueElement
                    .getAttributeValue(XmlConstants.XML_ATTRIBUTE_VALUE)));
        }

        double[] result = new double[values.size()];
        int i = 0;
        for (Double d : values) {
            result[i] = d;
            i++;
        }
        return result;
    }
}
