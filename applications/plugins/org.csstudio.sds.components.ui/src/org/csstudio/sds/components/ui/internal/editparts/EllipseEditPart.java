package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.EllipseElement;
import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipseFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;

/**
 * EditPart controller for <code>EllipseElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class EllipseEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		EllipseElement model = (EllipseElement) getCastedModel();
		
		RefreshableEllipseFigure ellipse = new RefreshableEllipseFigure();
		ellipse.setFill(model.getFillGrade());
		
		return ellipse;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure f) {
		RefreshableEllipseFigure ellipse = (RefreshableEllipseFigure) f;

		if (propertyName.equals(EllipseElement.PROP_FILL)) {
			ellipse.setFill((Double) newValue);
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// fill
		IElementPropertyChangeHandler fillHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshableEllipseFigure ellipse = (RefreshableEllipseFigure) figure;
				ellipse.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(EllipseElement.PROP_FILL, fillHandler);
	}

}
