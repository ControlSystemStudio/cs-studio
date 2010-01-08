package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;

/**
 *The model of spinner widget.
 * @author Xihui Chen
 */
public class SpinnerModel extends LabelModel {
	
	public final String ID = "org.csstudio.opibuilder.widgets.spinner";

	
	/** The ID of the minimum property. */
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The ID of the maximum property. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$	
	
	/**the amount the scrollbar will move when the up or down arrow buttons are
	pressed.*/	
	public static final String PROP_STEP_INCREMENT = "step_increment"; //$NON-NLS-1$		
	
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = Double.NEGATIVE_INFINITY;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = Double.POSITIVE_INFINITY;	
	
	private static final double DEFAULT_STEP_INCREMENT = 1;	
	
	
	public SpinnerModel() {		
		setSize(85, 25);
		setBorderStyle(BorderStyle.LOWERED);
	}
	
	
	@Override
	protected void configureProperties() {			
		pvModel = true;
		super.configureProperties();		
		removeProperty(LabelModel.PROP_AUTOSIZE);
		setPropertyVisible(LabelModel.PROP_TEXT, false);
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));			
		
		addProperty(new DoubleProperty(PROP_STEP_INCREMENT, "Step Increment", 
				WidgetPropertyCategory.Behavior, DEFAULT_STEP_INCREMENT));		
	
		
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
	 * @return the step increment
	 */
	public Double getStepIncrement() {
		return (Double) getProperty(PROP_STEP_INCREMENT).getPropertyValue();
	}
	
	
	
	@Override
	public String getTypeID() {
		return ID;
	}

	
	
}
