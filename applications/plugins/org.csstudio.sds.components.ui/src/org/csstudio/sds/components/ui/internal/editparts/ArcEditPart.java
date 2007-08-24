package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

import org.csstudio.sds.components.model.ArcModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableArcFigure;

/**
 * EditPart controller for the arc widget.
 * 
 * @author jbercic
 * 
 */
public final class ArcEditPart extends AbstractWidgetEditPart {

	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link ArcModel}
	 */
	protected ArcModel getCastedModel() {
		return (ArcModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ArcModel model = getCastedModel();
		// create AND initialize the view properly
		final RefreshableArcFigure figure = new RefreshableArcFigure();
		
		figure.setTransparent(model.getTransparent());
		figure.setBorderWidth(model.getBorderWidth());
		figure.setBorderColor(model.getBorderColor());
		figure.setStartAngle(model.getStartAngle());
		figure.setAngle(model.getAngle());
		figure.setLineWidth(model.getLineWidth());
		figure.setFill(model.getFill());
		figure.setFillColor(model.getFillColor());
		
		return figure;
	}
	
	/**
	 * Register color property change handlers.
	 */
	protected void registerColorPropertyHandlers() {
		// border
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setBorderColor((RGB)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_BORDER_COLOR, handle);
		
		// fill
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setFillColor((RGB)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_FILLCOLOR, handle);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// changes to the transparency property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setTransparent((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_TRANSPARENT, handle);
		
		// changes to the border width property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setBorderWidth((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_BORDER_WIDTH, handle);
		
		// changes to the start angle property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setStartAngle((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_STARTANGLE, handle);
		
		// changes to the angle property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setAngle((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_ANGLE, handle);
		
		// changes to the line width property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setLineWidth((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_LINEWIDTH, handle);
		
		// changes to the filled property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
				arcFigure.setFill((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_FILLED, handle);
		
		registerColorPropertyHandlers();
	}
}
