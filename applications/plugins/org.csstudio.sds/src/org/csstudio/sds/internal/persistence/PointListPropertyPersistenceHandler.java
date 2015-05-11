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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.jdom.Element;

/**
 * Persistence handler for the property type <code>sds.pointlist</code>.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class PointListPropertyPersistenceHandler extends
        AbstractPropertyPersistenceHandler {

    /**
     * XML tag name <code>pointList</code>.
     */
    public static final String XML_ELEMENT_POINT_LIST = "pointList"; //$NON-NLS-1$

    /**
     * XML tag name <code>point</code>.
     */
    public static final String XML_ELEMENT_POINT = "point"; //$NON-NLS-1$

    /**
     * XML attribute name <code>y</code>.
     */
    public static final String XML_ATTRIBUTE_Y = "y"; //$NON-NLS-1$

    /**
     * XML attribute name <code>x</code>.
     */
    public static final String XML_ATTRIBUTE_X = "x"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeProperty(final Element domElement,
            final Object propertyValue) {
        PointList pointList = (PointList) propertyValue;

        Element pointListElement = new Element(XML_ELEMENT_POINT_LIST);

        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.getPoint(i);

            Element pointElement = new Element(XML_ELEMENT_POINT);
            pointElement.setAttribute(XML_ATTRIBUTE_X, "" + point.x); //$NON-NLS-1$
            pointElement.setAttribute(XML_ATTRIBUTE_Y, "" + point.y); //$NON-NLS-1$
            pointListElement.addContent(pointElement);
        }

        domElement.addContent(pointListElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readProperty(final Element domElement) {
        Element pointListElement = domElement.getChild(XML_ELEMENT_POINT_LIST);

        PointList result = new PointList();

        for (Object o : pointListElement.getChildren(XML_ELEMENT_POINT)) {
            Element pointElement = (Element) o;

            String x = pointElement.getAttributeValue(XML_ATTRIBUTE_X);
            String y = pointElement.getAttributeValue(XML_ATTRIBUTE_Y);

            result
                    .addPoint(new Point(Integer.parseInt(x), Integer
                            .parseInt(y)));
        }

        return result;
    }
}
