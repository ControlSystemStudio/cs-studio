package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.GaugeModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableGaugeFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the Gauge widget. The controller mediates between
 * {@link GaugeModel} and {@link RefreshableGaugeFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class GaugeEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final GaugeModel model = (GaugeModel) getWidgetModel();

		RefreshableGaugeFigure gauge = new RefreshableGaugeFigure();
		
		initializeCommonFigureProperties(gauge, model);		
		gauge.setNeedleColor((model.getNeedleColor()));
		gauge.setEffect3D(model.isEffect3D());	
		gauge.setGradient(model.isRampGradient());
		
		return gauge;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//needle Color
		IWidgetPropertyChangeHandler needleColorColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableGaugeFigure gauge = (RefreshableGaugeFigure) refreshableFigure;
				gauge.setNeedleColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(GaugeModel.PROP_NEEDLE_COLOR, needleColorColorHandler);	
		
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableGaugeFigure gauge = (RefreshableGaugeFigure) refreshableFigure;
				gauge.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(GaugeModel.PROP_EFFECT3D, effect3DHandler);	
		
		//Ramp gradient
		IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableGaugeFigure gauge = (RefreshableGaugeFigure) refreshableFigure;
				gauge.setGradient((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(GaugeModel.PROP_RAMP_GRADIENT, gradientHandler);	
		
		
		
	}

}
