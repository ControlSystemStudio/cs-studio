package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * The model for checkbox widget 
 * @author Xihui Chen
 */
public class CheckBoxModel extends AbstractPVWidgetModel {
	
	
	
	/** Bit of the PV to be read and writtend.*/
	public static final String PROP_BIT = "bit"; //$NON-NLS-1$		
	
	/** Text of the label. */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	
	/**
	 * Font of the label.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	/** True if the widget size can be automatically adjusted along with the text size. */
	public static final String PROP_AUTOSIZE = "auto_size";	//$NON-NLS-1$
	
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.checkbox"; //$NON-NLS-1$
	
	public CheckBoxModel() {
		setSize(100, 20);
		setForegroundColor(new RGB(0,0,0));
	}
	
	@Override
	protected void configureProperties() {				
		addProperty(new IntegerProperty(PROP_BIT, "Bit",
				WidgetPropertyCategory.Behavior, 0, -1, 63));		
		addProperty(new StringProperty(PROP_LABEL, "Label",
				WidgetPropertyCategory.Display, ""));	//$NON-NLS-1$	
		addProperty(new FontProperty(PROP_FONT, "Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 9, SWT.NONE))); //$NON-NLS-1$
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size", 
				WidgetPropertyCategory.Display, false));
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
	
	public boolean isAutoSize(){
		return (Boolean)getCastedPropertyValue(PROP_AUTOSIZE);
	}
}
