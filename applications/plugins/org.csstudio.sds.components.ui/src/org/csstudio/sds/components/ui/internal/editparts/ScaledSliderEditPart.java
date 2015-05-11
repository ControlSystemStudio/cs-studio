package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ScaledSliderModel;
import org.csstudio.sds.components.ui.internal.figures.ScaledSliderFigure;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * EditPart controller for the scaled slider widget. The controller mediates
 * between {@link ScaledSliderModel} and {@link ScaledSliderFigure}.
 *
 * @author Xihui Chen
 *
 */
public final class ScaledSliderEditPart extends AbstractMarkedWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final ScaledSliderModel model = (ScaledSliderModel) getWidgetModel();

        ScaledSliderFigure slider = new ScaledSliderFigure();

        initializeCommonFigureProperties(slider, model);
        slider.setFillColor(getModelColor(ScaledSliderModel.PROP_FILL_COLOR));
        slider.setEffect3D(model.isEffect3D());
        slider.setFillBackgroundColor(getModelColor(ScaledSliderModel.PROP_FILLBACKGROUND_COLOR));
        slider.setThumbColor(getModelColor(ScaledSliderModel.PROP_THUMB_COLOR));
        slider.setHorizontal(model.isHorizontal());
        slider.setIncrement(model.getIncrement());
        slider.addSliderListener(new ScaledSliderFigure.IScaledSliderListener() {
            public void sliderValueChanged(final double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    model.setPropertyManualValue(ScaledSliderModel.PROP_VALUE, newValue);
                }

            }
        });

        return slider;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        // fillColor
        setPropertyChangeHandler(ScaledSliderModel.PROP_FILL_COLOR, new ColorChangeHandler<ScaledSliderFigure>() {
            @Override
            protected void doHandle(ScaledSliderFigure figure, Color color) {
                figure.setFillColor(color);
            }
        });

        // fillBackgroundColor
        setPropertyChangeHandler(ScaledSliderModel.PROP_FILLBACKGROUND_COLOR, new ColorChangeHandler<ScaledSliderFigure>() {
            @Override
            protected void doHandle(ScaledSliderFigure figure, Color color) {
                figure.setFillBackgroundColor(color);
            }
        });

        // thumbColor
        setPropertyChangeHandler(ScaledSliderModel.PROP_THUMB_COLOR, new ColorChangeHandler<ScaledSliderFigure>() {
            @Override
            protected void doHandle(ScaledSliderFigure figure, Color color) {
                figure.setThumbColor(color);
            }
        });

        // effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_EFFECT3D, effect3DHandler);

        // horizontal
        IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setHorizontal((Boolean) newValue);
                ScaledSliderModel model = (ScaledSliderModel) getModel();

                if ((Boolean) newValue) // from vertical to horizontal
                    model.setLocation(model.getX() - model.getHeight() / 2 + model.getWidth() / 2, model.getY() + model.getHeight() / 2
                            - model.getWidth() / 2);
                else
                    // from horizontal to vertical
                    model.setLocation(model.getX() + model.getWidth() / 2 - model.getHeight() / 2, model.getY() - model.getWidth() / 2
                            + model.getHeight() / 2);

                model.setSize(model.getHeight(), model.getWidth());

                return true;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_HORIZONTAL, horizontalHandler);

        // enabled. WidgetBaseEditPart will force the widget as disabled in edit
        // model,
        // which is not the case for the scaled slider
        IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;

                // slider.setEnabled((Boolean) newValue);
                // 2009-07-21 KM: Changed to mkae the ScaledSlider only editable
                // when the user has the permission for it
                slider.setEnabled(getWidgetModel().isAccesible());
                return true;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_ENABLED, enableHandler);

        IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
                ScaledSliderFigure slider = (ScaledSliderFigure) refreshableFigure;
                slider.setIncrement((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ScaledSliderModel.PROP_INCREMENT, incrementHandler);

    }

}
