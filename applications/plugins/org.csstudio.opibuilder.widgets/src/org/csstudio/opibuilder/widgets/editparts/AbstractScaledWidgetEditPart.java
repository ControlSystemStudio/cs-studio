package org.csstudio.opibuilder.widgets.editparts;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.AbstractScaledWidgetModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.sds.components.ui.internal.figures.AbstractScaledWidgetFigure;
import org.csstudio.utility.pv.PV;
import org.eclipse.draw2d.IFigure;

/**
 * Base editPart controller for a widget based on {@link AbstractScaledWidgetModel}.
 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractScaledWidgetEditPart extends AbstractPVWidgetEditPart {

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link AbstractScaledWidgetFigure} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link AbstractBaseEditPart#doCreateFigure()}.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected void initializeCommonFigureProperties(
			final AbstractScaledWidgetFigure figure, final AbstractScaledWidgetModel model) {
		
		figure.setRange(model.getMinimum(), model.getMaximum());
		figure.setMajorTickMarkStepHint(model.getMajorTickStepHint());
		figure.setLogScale(model.isLogScaleEnabled());
		figure.setShowScale(model.isShowScale());
		figure.setShowMinorTicks(model.isShowMinorTicks());	
		figure.setTransparent(model.isTransparent());
		
		
	}	
	
	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractScaledWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setValue(ValueUtil.getDouble((IValue)newValue));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);
		
		//minimum
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setRange((Double) newValue, ((AbstractScaledWidgetModel)getModel()).getMaximum());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MIN, minimumHandler);
		
		//maximum
		IWidgetPropertyChangeHandler maximumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setRange(((AbstractScaledWidgetModel)getModel()).getMinimum(), (Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MAX, maximumHandler);
		
		//major tick step hint
		IWidgetPropertyChangeHandler majorTickHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setMajorTickMarkStepHint((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_MAJOR_TICK_STEP_HINT, majorTickHandler);
	
		
		
		//logScale
		IWidgetPropertyChangeHandler logScaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setLogScale((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_LOG_SCALE, logScaleHandler);
		
		//showScale
		IWidgetPropertyChangeHandler showScaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setShowScale((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SHOW_SCALE, showScaleHandler);
		
	
		//showMinorTicks
		IWidgetPropertyChangeHandler showMinorTicksHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setShowMinorTicks((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_SHOW_MINOR_TICKS, showMinorTicksHandler);
		
		//Transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_TRANSPARENT, transparentHandler);
		
	}

}
