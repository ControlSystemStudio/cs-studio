package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;


/**The model for text input.
 * @author Xihui Chen
 *
 */
public class TextInputModel extends TextIndicatorModel {
	
	/** The minimum input value allowed.*/
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The maximum input value allowed. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$	
	
	/** Load limit from PV. */
	public static final String PROP_LIMITS_FROM_PV = "limits_from_pv"; //$NON-NLS-1$		
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = Double.NEGATIVE_INFINITY;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = Double.POSITIVE_INFINITY;	
	
	public TextInputModel() {
		setPropertyValue(PROP_LIMITS_FROM_PV, false);
	}
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextInput"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();	
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));	
		
		addProperty(new BooleanProperty(PROP_LIMITS_FROM_PV, "Limits From PV",
				WidgetPropertyCategory.Behavior, true));
		setText(""); //$NON-NLS-1$
		setBorderStyle(BorderStyle.LOWERED);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
	}
	
	
	/**
	 * @return the minimum value
	 */
	public Double getMinimum() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}


	/**
	 * @return the maximum value
	 */
	public Double getMaximum() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}
	
	/**
	 * @return true if limits will be load from DB, false otherwise
	 */
	public boolean isLimitsFromPV() {
		return (Boolean) getProperty(PROP_LIMITS_FROM_PV).getPropertyValue();
	}
}
