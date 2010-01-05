package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 *The model of scroll bar widget.
 * @author Xihui Chen
 */
public class ScrollBarModel extends AbstractPVWidgetModel {
	
	public final String ID = "org.csstudio.opibuilder.widgets.scrollbar";

	
	/** The ID of the minimum property. */
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The ID of the maximum property. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$	

	/** The amount the scrollbar will move when the page up or page down areas are
	pressed.*/
	public static final String PROP_PAGE_INCREMENT = "page_increment"; //$NON-NLS-1$		
	
	/**the amount the scrollbar will move when the up or down arrow buttons are
	pressed.*/	
	public static final String PROP_STEP_INCREMENT = "step_increment"; //$NON-NLS-1$	
	
	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	/**
	 * The ID of the pv name property.
	 */
	public static final String PROP_CONTROL_PV= "control_pv"; //$NON-NLS-1$
	
	/**
	 * The ID of the pv value property.
	 */
	public static final String PROP_CONTROL_PV_VALUE= "control_pv_value"; //$NON-NLS-1$
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = 100;	
	
	private static final double DEFAULT_PAGE_INCREMENT = 20;	
	private static final double DEFAULT_STEP_INCREMENT = 1;	

	@Override
	protected void configureProperties() {		
		addPVProperty(new StringProperty(PROP_CONTROL_PV, "Control PV", WidgetPropertyCategory.Basic,
		""), new PVValueProperty(PROP_CONTROL_PV_VALUE, null));
		
		
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));			
		
		addProperty(new DoubleProperty(PROP_PAGE_INCREMENT, "Page Increment", 
				WidgetPropertyCategory.Behavior, DEFAULT_PAGE_INCREMENT));
		
		addProperty(new DoubleProperty(PROP_STEP_INCREMENT, "Step Increment", 
				WidgetPropertyCategory.Behavior, DEFAULT_STEP_INCREMENT));			
		
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal", 
				WidgetPropertyCategory.Display, true));
		
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
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();
	}

	/**
	 * @return the page increment
	 */
	public Double getPageIncrement() {
		return (Double) getProperty(PROP_PAGE_INCREMENT).getPropertyValue();
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
