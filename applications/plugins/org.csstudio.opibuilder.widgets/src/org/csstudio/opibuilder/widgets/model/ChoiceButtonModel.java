package org.csstudio.opibuilder.widgets.model;

import org.eclipse.swt.graphics.RGB;

/**Model of Choice Button.
 * @author Xihui Chen
 *
 */
public class ChoiceButtonModel extends AbstractChoiceModel {

	public final String ID = "org.csstudio.opibuilder.widgets.choiceButton";

	public ChoiceButtonModel() {
		setBackgroundColor(new RGB(240,240,240));
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	

}
