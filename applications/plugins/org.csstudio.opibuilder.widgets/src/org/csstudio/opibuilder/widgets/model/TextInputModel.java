package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.visualparts.BorderStyle;


/**The model for text input.
 * @author Xihui Chen
 *
 */
public class TextInputModel extends TextIndicatorModel {
	
	
	
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextInput"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();	
		setText(""); //$NON-NLS-1$
		setBorderStyle(BorderStyle.LOWERED);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
	}
	
}
