package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.widgets.model.ChoiceButtonModel;
import org.csstudio.swt.widgets.figures.AbstractChoiceFigure;
import org.csstudio.swt.widgets.figures.ChoiceButtonFigure;

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
