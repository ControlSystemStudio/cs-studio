package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.swt.widgets.figures.AbstractChoiceFigure;
import org.csstudio.swt.widgets.figures.RadioBoxFigure;

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
