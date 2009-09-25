package org.csstudio.diag.diles.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.csstudio.diag.diles.actions.OneStepAction;
import org.csstudio.diag.diles.figures.ChartFigure;
import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.model.AbstractChartElement;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Chart;
import org.csstudio.diag.diles.model.HardwareTrueFalse;
import org.csstudio.diag.diles.policies.LayoutPolicy;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

public class ChartPart extends AppAbstractEditPart {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutPolicy());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createChartFigure();
	}

	public void draw(int x1, int y1, int x2, int y2) {
		((ChartFigure) getFigure()).setForegroundColor(ColorConstants.darkBlue);

		((ChartFigure) getFigure()).drawLine(x1, y1, x2, y2);
	}

	protected Chart getChart() {
		return (Chart) getModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List getModelChildren() {
		return getChart().getChildren();
	}

	protected void makeActiveFigure() {
		int col = OneStepAction.getCurrentColumn();
		draw(col * 80, 0, col * 80, 80000);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Chart.CHILD.equals(prop) || Activity.ACTIVITY_STATUS.equals(prop)
				|| Activity.SOURCES.equals(prop)
				|| Activity.TARGETS.equals(prop)) {
			refreshChildren();
		}

		if (HardwareTrueFalse.HARDWARE_TRUE_FALSE.equals(prop)) {
			refreshVisuals();
		}
		if (AbstractChartElement.ACTIVE_COLUMN.equals(prop)) {
			makeActiveFigure();
		}

	}
}