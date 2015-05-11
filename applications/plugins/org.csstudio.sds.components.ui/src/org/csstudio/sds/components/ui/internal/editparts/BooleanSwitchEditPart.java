package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.BooleanSwitchModel;
import org.csstudio.sds.components.ui.internal.figures.BoolSwitchFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 *
 * @author Kai Meyer (C1 WPS)
 *
 */
public class BooleanSwitchEditPart extends AbstractWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final BooleanSwitchModel model = (BooleanSwitchModel) getWidgetModel();
        BoolSwitchFigure figure = new BoolSwitchFigure();
        figure.setEffect3D(model.get3dEffect());
        figure.setBooleanValue(model.getValue());
        figure.setOffColor(getModelColor(BooleanSwitchModel.PROP_OFF_COLOR));
        figure.setOnColor(getModelColor(BooleanSwitchModel.PROP_ON_COLOR));
        figure.setShowBooleanLabel(model.getShowLabels());
        figure.setOnLabel(model.getOnLabel());
        figure.setOffLabel(model.getOffLabel());
        figure.addBoolControlListener(new IBoolControlListener() {
            public void valueChanged(boolean newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    model.setValue(newValue);
                }
            }
        });
        figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
        figure.setToggle(true);
        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // value
        IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolSwitchFigure switchFigure = (BoolSwitchFigure) refreshableFigure;
                switchFigure.setBooleanValue((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BooleanSwitchModel.PROP_VALUE, valueHandler);

        // 3d effect
        IWidgetPropertyChangeHandler effectHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolSwitchFigure switchFigure = (BoolSwitchFigure) refreshableFigure;
                switchFigure.setEffect3D((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BooleanSwitchModel.PROP_3D_EFFECT, effectHandler);

        // on color
        setPropertyChangeHandler(BooleanSwitchModel.PROP_ON_COLOR, new ColorChangeHandler<BoolSwitchFigure>(){
            @Override
            protected void doHandle(BoolSwitchFigure figure, Color color) {
                figure.setOnColor(color);
            }
        });

        // off color
        setPropertyChangeHandler(BooleanSwitchModel.PROP_OFF_COLOR, new ColorChangeHandler<BoolSwitchFigure>(){
            @Override
            protected void doHandle(BoolSwitchFigure figure, Color color) {
                figure.setOffColor(color);
            }
        });

        // on label
        IWidgetPropertyChangeHandler onLabelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolSwitchFigure switchFigure = (BoolSwitchFigure) refreshableFigure;
                switchFigure.setOnLabel((String) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BooleanSwitchModel.PROP_ON_LABEL, onLabelHandler);

        // on label
        IWidgetPropertyChangeHandler offLabelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolSwitchFigure switchFigure = (BoolSwitchFigure) refreshableFigure;
                switchFigure.setOffLabel((String) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BooleanSwitchModel.PROP_OFF_LABEL, offLabelHandler);

        // value
        IWidgetPropertyChangeHandler showLabelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolSwitchFigure switchFigure = (BoolSwitchFigure) refreshableFigure;
                switchFigure.setShowBooleanLabel((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BooleanSwitchModel.PROP_LABEL_VISIBLE, showLabelHandler);
    }

}
