package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.widgets.figures.AbstractChoiceFigure;
import org.csstudio.opibuilder.widgets.figures.ChoiceButtonFigure;
import org.csstudio.opibuilder.widgets.model.ChoiceButtonModel;

/**The editpart of choice button widget.
 * @author Xihui Chen
 *
 */
public class ChoiceButtonEditpart extends AbstractChoiceEditPart {

	@Override
	protected AbstractChoiceFigure createChoiceFigure() {
		ChoiceButtonFigure figure = new ChoiceButtonFigure();
		return figure;
	}
	
	@Override
	public ChoiceButtonModel getWidgetModel() {
		return (ChoiceButtonModel)getModel();
	}
	

}
