package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.widgets.figures.AbstractChoiceFigure;
import org.csstudio.opibuilder.widgets.figures.RadioBoxFigure;

/**Editpart of Radio Box widget.
 * @author Xihui Chen
 *
 */
public class RadioBoxEditpart extends AbstractChoiceEditPart {

	@Override
	protected AbstractChoiceFigure createChoiceFigure() {
		return new RadioBoxFigure();
	}

}
