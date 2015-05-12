package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.KnobModel;
import org.csstudio.sds.components.ui.internal.figures.KnobFigure;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * EditPart controller for the knob widget. The controller mediates between
 * {@link KnobModel} and {@link KnobFigure}.
 *
 * @author Xihui Chen
 *
 */
public final class KnobEditPart extends AbstractMarkedWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final KnobModel model = (KnobModel) getWidgetModel();

        KnobFigure knob = new KnobFigure();

        initializeCommonFigureProperties(knob, model);
        knob.setBulbColor(getModelColor(KnobModel.PROP_KNOB_COLOR));
        knob.setEffect3D(model.isEffect3D());
        knob.setThumbColor(getModelColor(KnobModel.PROP_THUMB_COLOR));
        knob.setValueLabelVisibility(model.isShowValueLabel());
        knob.setGradient(model.isRampGradient());
        knob.setIncrement(model.getIncrement());
        knob.addKnobListener(new KnobFigure.IKnobListener() {
            public void knobValueChanged(final double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    model.setPropertyManualValue(KnobModel.PROP_VALUE, newValue);
                }
            }
        });

        return knob;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //knob color
        setPropertyChangeHandler(KnobModel.PROP_KNOB_COLOR, new ColorChangeHandler<KnobFigure>(){
            @Override
            protected void doHandle(KnobFigure figure, Color color) {
                figure.setBulbColor(color);
            }
        });


        //thumbColor
        setPropertyChangeHandler(KnobModel.PROP_THUMB_COLOR, new ColorChangeHandler<KnobFigure>(){
            @Override
            protected void doHandle(KnobFigure figure, Color color) {
                figure.setThumbColor(color);
            }
        });

        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_EFFECT3D, effect3DHandler);


        //show value label
        IWidgetPropertyChangeHandler valueLabelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setValueLabelVisibility((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_SHOW_VALUE_LABEL, valueLabelHandler);

        //Ramp gradient
        IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setGradient((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_RAMP_GRADIENT, gradientHandler);

        //enabled. WidgetBaseEditPart will force the widget as disabled in edit model,
        //which is not the case for the knob
        IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
//                knob.setEnabled((Boolean) newValue);
                // 2009-07-21 KM: Changed to mkae the ScaledSlider only editable when the user has the permission for it
                knob.setEnabled(getWidgetModel().isAccesible());
                return true;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_ENABLED, enableHandler);

        //increment
        IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                KnobFigure knob = (KnobFigure) refreshableFigure;
                knob.setIncrement((Double)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(KnobModel.PROP_INCREMENT, incrementHandler);

    }

}
