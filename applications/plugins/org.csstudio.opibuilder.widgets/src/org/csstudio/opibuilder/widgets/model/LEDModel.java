/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.figures.LEDFigure;
import org.eclipse.swt.graphics.Color;


/**
 * The widget model for LED.
 * @author Xihui Chen
 *
 */
public class LEDModel extends AbstractBoolWidgetModel {

	
	/** Generic category class. This could be used generally if the equivalent does not already exist. */
	public class GenericCategory implements WidgetPropertyCategory {
		public String label;
		public GenericCategory(String label) {
			this.label = label;
		}
		@Override
		public String toString() {
			return label;
		}
	}
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$
	
	/** The ID of the square LED property. */
	public static final String PROP_SQUARE_LED = "square_led"; //$NON-NLS-1$
	
	/** Number of states for this multi state widget */
	public static final String PROP_NSTATES = "state_count"; //$NON-NLS-1$

	/** Label text for multi state X */
	public static final String PROP_STATE_LABEL = "state_label_%s";
	
	/** Widget color for multi state X */
	public static final String PROP_STATE_COLOR = "state_color_%s";
	
	/** Description for widget color properties */
	public static final String DESC_STATE_COLOR = "State Color %s";
	
	/** State value for multi state X */
	public static final String PROP_STATE_VALUE = "state_value_%s";
	
	/** Description for state value properties */
	public static final String DESC_STATE_VALUE = "State Value %s";
	
	/** State fallback label property */
	public static final String PROP_STATE_FALLBACK_LABEL = "state_label_fallback";
	
	/** State fallback color property */
	public static final String PROP_STATE_FALLBACK_COLOR = "state_color_fallback";
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 20;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 20;
	
	public static final int MINIMUM_SIZE = 10;

	
	public LEDModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setScaleOptions(true, true, true);
	}
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true));
		
		addProperty(new BooleanProperty(PROP_SQUARE_LED, "Square LED", 
				WidgetPropertyCategory.Display, false));
		setPropertyVisible(PROP_BOOL_LABEL_POS, false);
		
		addProperty(new IntegerProperty(PROP_NSTATES,
				"State Count", WidgetPropertyCategory.Behavior, 2, 2, LEDFigure.MAX_NSTATES));
		setPropertyVisibleAndSavable(PROP_NSTATES, true, false);
		
		WidgetPropertyCategory category = new GenericCategory("State Fallback");
		
		addProperty(new StringProperty(PROP_STATE_FALLBACK_LABEL,
				"Label", category, LEDFigure.DEFAULT_STATE_FALLBACK_LABAL));
		setPropertyVisibleAndSavable(PROP_STATE_FALLBACK_LABEL, false, false);
		
		addProperty(new ColorProperty(PROP_STATE_FALLBACK_COLOR,
				"Color", category, LEDFigure.DEFAULT_STATE_FALLBACK_COLOR.getRGB()));
		setPropertyVisibleAndSavable(PROP_STATE_FALLBACK_COLOR, false, false);
		
		for(int state=0; state<LEDFigure.MAX_NSTATES; state++) {
			
			category = new GenericCategory(String.format("State %02d", state+1));
						
			addProperty(new StringProperty(String.format(PROP_STATE_LABEL, state),
					"Label", category, LEDFigure.DEFAULT_STATE_LABELS[state]));
			setPropertyVisibleAndSavable(String.format(PROP_STATE_LABEL, state), false, false);
			
			addProperty(new ColorProperty(String.format(PROP_STATE_COLOR, state),
					"Color", category, LEDFigure.DEFAULT_STATE_COLORS[state].getRGB()));
			setPropertyVisibleAndSavable(String.format(PROP_STATE_COLOR, state), false, false);
			
			addProperty(new  DoubleProperty(String.format(PROP_STATE_VALUE, state),
					"Value", category, LEDFigure.DEFAULT_STATE_VALUES[state]));
			setPropertyVisibleAndSavable(String.format(PROP_STATE_VALUE, state), false, false);
		}
	}
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.LED"; //$NON-NLS-1$	
	
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	/**
	 * @return true if the LED is square, false otherwise
	 */
	public boolean isSquareLED() {
		return (Boolean) getProperty(PROP_SQUARE_LED).getPropertyValue();
	}
	
	public int getNStates() {
		return (Integer) getProperty(PROP_NSTATES).getPropertyValue();
	}
	
	public String getStateLabel(int state) {
		return (String) getProperty(String.format(PROP_STATE_LABEL, state)).getPropertyValue();
	}
	
	public double getStateValue(int state) {
		return (Double) getProperty(String.format(PROP_STATE_VALUE, state)).getPropertyValue();
	}
	
	public Color getStateColor(int state) {
		return getSWTColorFromColorProperty(String.format(PROP_STATE_COLOR, state));
	}
	
	public String getStateFallbackLabel() {
		return (String) getProperty(PROP_STATE_FALLBACK_LABEL).getPropertyValue();
	}
	
	public Color getStateFallbackColor() {
		return getSWTColorFromColorProperty(PROP_STATE_FALLBACK_COLOR);
	}
}
