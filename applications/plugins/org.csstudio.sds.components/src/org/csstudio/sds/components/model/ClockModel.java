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
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * An (very) simple clock model.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ClockModel extends AbstractWidgetModel {
	/**
	 * The ID of the time property.
	 */
	public static final String PROP_TIME = "time"; //$NON-NLS-1$

	/**
	 * The ID of the date pattern property.
	 */
	public static final String PROP_PATTERN = "pattern"; //$NON-NLS-1$

	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	/**
	 * The ID of the text alignment property.
	 */
	public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "element.clock"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;

	/**
	 * The default value of the text alignment property.
	 */
	private static final int DEFAULT_TEXT_ALIGNMENT = 0;

	private static final String[] SHOW_LABELS = new String[] { "Center", "Top",
			"Bottom", "Left", "Right" };

	/**
	 * Standard constructor.
	 */
	public ClockModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_TIME, new DoubleProperty("Time",
				WidgetPropertyCategory.Display, System.currentTimeMillis())); //$NON-NLS-1$
		addProperty(PROP_PATTERN, new StringProperty("Pattern",
				WidgetPropertyCategory.Display, "HH:mm:ss:SSS")); //$NON-NLS-1$
		addProperty(PROP_FONT, new FontProperty("Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 12, SWT.BOLD))); //$NON-NLS-1$
		addProperty(PROP_TEXT_ALIGNMENT, new OptionProperty("Text Alignment",
				WidgetPropertyCategory.Display, SHOW_LABELS,
				DEFAULT_TEXT_ALIGNMENT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_TIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColorTestProperty() {
		return PROP_COLOR_BACKGROUND;
	}

	/**
	 * Return the time value.
	 * 
	 * @return The time value.
	 */
	public Double getTime() {
		return (Double) getProperty(PROP_TIME).getPropertyValue();
	}

	/**
	 * Return the date pattern.
	 * 
	 * @return The date pattern.
	 */
	public String getDatePattern() {
		return (String) getProperty(PROP_PATTERN).getPropertyValue();
	}

	/**
	 * Return the label font.
	 * 
	 * @return The label font.
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}

	/**
	 * Gets, if the marks should be shown or not.
	 * 
	 * @return int 0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGNMENT).getPropertyValue();
	}
}
