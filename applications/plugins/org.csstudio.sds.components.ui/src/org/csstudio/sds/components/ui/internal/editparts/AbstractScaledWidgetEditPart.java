package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractScaledWidgetModel;
import org.csstudio.sds.components.ui.internal.figures.AbstractScaledWidgetFigure;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * Base editPart controller for a widget based on {@link AbstractScaledWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractScaledWidgetEditPart extends AbstractWidgetEditPart {

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
        figure.setValue(model.getValue());
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
        setValueHandler();
        setMinRangeHandler();
        setMaxRangeHandler();
        setMajorTickMarkStepHintHandler();
        setLogScaleHandler();
        setShowScaleHandler();
        setShowMinorTicksHandler();
        setTransparentHandler();
    }

    /**
     *
     */
    private void setTransparentHandler() {
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

    /**
     *
     */
    private void setShowMinorTicksHandler() {
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
    }

    /**
     *
     */
    private void setShowScaleHandler() {
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
    }

    /**
     *
     */
    private void setLogScaleHandler() {
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
    }

    /**
     *
     */
    private void setMajorTickMarkStepHintHandler() {
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
    }

    /**
     *
     */
    private void setMaxRangeHandler() {
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
    }

    /**
     *
     */
    private void setMinRangeHandler() {
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
    }

    /**
     *
     */
    private void setValueHandler() {
        IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                AbstractScaledWidgetFigure figure = (AbstractScaledWidgetFigure) refreshableFigure;
                figure.setValue((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractScaledWidgetModel.PROP_VALUE, valueHandler);
    }

}
