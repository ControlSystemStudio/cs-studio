package org.csstudio.opibuilder.widgets.model;

import org.csstudio.platform.ui.util.CustomMediaFactory;


/**Model of Radio Box.
 * @author Xihui Chen
 *
 */
public class RadioBoxModel extends AbstractChoiceModel {

	public final String ID = "org.csstudio.opibuilder.widgets.radioBox";

	public RadioBoxModel() {
		setPropertyValue(PROP_SELECTED_COLOR, CustomMediaFactory.COLOR_BLACK);
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	

}
