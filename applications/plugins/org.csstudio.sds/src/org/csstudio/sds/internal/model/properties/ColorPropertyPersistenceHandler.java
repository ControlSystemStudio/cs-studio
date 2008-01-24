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
package org.csstudio.sds.internal.model.properties;

import org.eclipse.swt.graphics.RGB;
import org.jdom.Element;

/**
 * Persistence handler for the property type <code>sds.color</code>.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ColorPropertyPersistenceHandler extends
		AbstractPropertyPersistenceHandler {

	/**
	 * XML attribute name <code>color</code>.
	 */
	public static final String XML_ELEMENT_COLOR = "color"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>red</code>.
	 */
	public static final String XML_ATTRIBUTE_RED = "red"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>green</code>.
	 */
	public static final String XML_ATTRIBUTE_GREEN = "green"; //$NON-NLS-1$

	/**
	 * XML attribute name <code>blue</code>.
	 */
	public static final String XML_ATTRIBUTE_BLUE = "blue"; //$NON-NLS-1$	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeProperty(final Element domElement,
			final Object propertyValue) {
		Element colorElement = new Element(XML_ELEMENT_COLOR);

		RGB color = (RGB) propertyValue;
		colorElement.setAttribute(XML_ATTRIBUTE_RED, "" + color.red); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_GREEN, "" + color.green); //$NON-NLS-1$
		colorElement.setAttribute(XML_ATTRIBUTE_BLUE, "" + color.blue); //$NON-NLS-1$

		domElement.addContent(colorElement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object readProperty(final Element domElement) {
		Element colorElement = domElement.getChild(XML_ELEMENT_COLOR);

		String red = colorElement.getAttributeValue(XML_ATTRIBUTE_RED);
		String green = colorElement.getAttributeValue(XML_ATTRIBUTE_GREEN);
		String blue = colorElement.getAttributeValue(XML_ATTRIBUTE_BLUE);

		return new RGB(Integer.parseInt(red), Integer.parseInt(green), Integer
				.parseInt(blue));
	}

}
