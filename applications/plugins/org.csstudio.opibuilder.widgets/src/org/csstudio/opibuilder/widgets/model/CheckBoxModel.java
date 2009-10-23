package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * The model for checkbox widget 
 * @author Xihui Chen
 */
public class CheckBoxModel extends AbstractPVWidgetModel {
	
	
	
	/** The ID of the bit property. */
	public static final String PROP_BIT = "bit"; //$NON-NLS-1$		
	
	/** The ID of the label property. */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.checkbox";
	
	@Override
	protected void configureProperties() {				
		addProperty(new IntegerProperty(PROP_BIT, "Bit",
				WidgetPropertyCategory.Behavior, 0, -1, 63));		
		addProperty(new StringProperty(PROP_LABEL, "Label",
				WidgetPropertyCategory.Display, ""));	//$NON-NLS-1$	
		addProperty(new FontProperty(PROP_FONT, "Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 9, SWT.NONE))); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
	/**
	 * @return the bit. If bit is -1, the value channel must be enum, otherwise, 
	 * it must be numeric value 
	 */
	public Integer getBit() {
		return (Integer) getProperty(PROP_BIT).getPropertyValue();
	}


	/**
	 * @return the on label
	 */
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
	}

	
	
	/**
	 * Return the label font.
	 * 
	 * @return The label font.
	 */
	public OPIFont getFont() {
		return (OPIFont) getProperty(PROP_FONT).getPropertyValue();
	}
}
