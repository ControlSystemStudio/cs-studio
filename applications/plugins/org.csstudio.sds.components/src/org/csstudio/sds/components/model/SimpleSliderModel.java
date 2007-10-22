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
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * An ellipse widget model.
 * 
 * @author Sven Wende, Alexander Will
 * @version $Revision$
 * 
 */
public final class SimpleSliderModel extends AbstractWidgetModel {

	/**
	 * The ID of the "show value as text" property.
	 */
	public static final String PROP_SHOW_VALUE_AS_TEXT = "showValueAsText"; //$NON-NLS-1$

	/**
	 * The ID of the value property.
	 */
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$

	/**
	 * The ID of the minimum property.
	 */
	public static final String PROP_MIN = "min"; //$NON-NLS-1$

	/**
	 * The ID of the maximum property.
	 */
	public static final String PROP_MAX = "max"; //$NON-NLS-1$

	/**
	 * The ID of the increment property.
	 */
	public static final String PROP_INCREMENT = "increment"; //$NON-NLS-1$

	/**
	 * The ID of the precision property.
	 */
	public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$

	/**
	 * The ID of the minimum slider width property.
	 */
	public static final String PROP_SLIDER_WIDTH = "sliderWidth"; //$NON-NLS-1$

	/**
	 * The ID of the orientation property.
	 */
	public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.SimpleSlider"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 10;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * Standard constructor.
	 * 
	 */
	public SimpleSliderModel() {
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
		addProperty(PROP_VALUE, new DoubleProperty("Slider Value",
				WidgetPropertyCategory.Behaviour, 50.0));
		addProperty(PROP_SHOW_VALUE_AS_TEXT, new BooleanProperty(
				"Show Value As Text", WidgetPropertyCategory.Display, false));
		addProperty(PROP_MIN, new DoubleProperty("Min",
				WidgetPropertyCategory.Behaviour, 0.0));
		addProperty(PROP_MAX, new DoubleProperty("Max",
				WidgetPropertyCategory.Behaviour, 100.0));
		addProperty(PROP_INCREMENT, new DoubleProperty("Increment",
				WidgetPropertyCategory.Behaviour, 1.0));
		addProperty(PROP_ORIENTATION, new BooleanProperty(
				"Horizontal orientation", WidgetPropertyCategory.Display, true));
		addProperty(PROP_PRECISION, new IntegerProperty("Decimal places",
				WidgetPropertyCategory.Behaviour, 2, 0, 5));
		addProperty(PROP_SLIDER_WIDTH, new IntegerProperty("Slider wide",
				WidgetPropertyCategory.Display, 5, 0, Integer.MAX_VALUE));

		setBackgroundColor(new RGB(255, 255, 255));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_NAME)+"\n");
		buffer.append("Maximum:\t");
		buffer.append(createParameter(PROP_MAX)+"\n");
		buffer.append("Minimum:\t");
		buffer.append(createParameter(PROP_MIN)+"\n");
		buffer.append("Value:\t\t");
		buffer.append(createParameter(PROP_VALUE));
		return buffer.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}

	/**
	 * Return the min value.
	 * 
	 * @return The min value.
	 */
	public double getMin() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}

	/**
	 * Return the max value.
	 * 
	 * @return The max value.
	 */
	public double getMax() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}

	/**
	 * Return the increment value.
	 * 
	 * @return The increment value.
	 */
	public double getIncrement() {
		return (Double) getProperty(PROP_INCREMENT).getPropertyValue();
	}

	/**
	 * Return the current slider value.
	 * 
	 * @return The current slider value.
	 */
	public double getValue() {
		return (Double) getProperty(PROP_VALUE).getPropertyValue();
	}

	/**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return (Integer) getProperty(PROP_PRECISION).getPropertyValue();
	}

	/**
	 * Return the slider width.
	 * 
	 * @return The slider width.
	 */
	public int getSliderWidth() {
		return (Integer) getProperty(PROP_SLIDER_WIDTH).getPropertyValue();
	}

	/**
	 * Return whether the slider has a horizontal or a vertical orientation.
	 * 
	 * @return True if the slider has a horizontal orientation.
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_ORIENTATION).getPropertyValue();
	}

	/**
	 * Return whether the slider value should also be displayed as a text.
	 * 
	 * @return True if the slider value should also be displayed as a text.
	 */
	public boolean isShowValueAsText() {
		return (Boolean) getProperty(PROP_SHOW_VALUE_AS_TEXT)
				.getPropertyValue();
	}
}
