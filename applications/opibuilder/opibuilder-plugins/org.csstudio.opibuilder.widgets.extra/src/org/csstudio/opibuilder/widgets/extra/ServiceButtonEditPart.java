package org.csstudio.opibuilder.widgets.extra;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.ServiceMethodDescription;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.opibuilder.widgets.model.CheckBoxModel;
import org.csstudio.utility.pvmanager.widgets.ServiceButton;
import org.eclipse.draw2d.IFigure;

public class ServiceButtonEditPart extends AbstractWidgetEditPart {

    /**
     * Create and initialize figure.
     */
    @Override
    protected ServiceButtonFigure doCreateFigure() {
    ServiceButtonFigure figure = new ServiceButtonFigure(this);
    configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
    return figure;
    }

    private static void configure(ServiceButton widget,
        ServiceButtonModel model, boolean runMode) {
    if (runMode) {
        widget.setLabel(model.getLabel());
        ServiceMethodDescription serviceMethodDescription = model.getServiceMethodDescription();
        widget.setServiceName(serviceMethodDescription.getService() + "/"
            + serviceMethodDescription.getMethod());
        widget.configureArgumentMap(serviceMethodDescription.getArgumentPvs());
        widget.configureResultMap(serviceMethodDescription.getResultPvs());
    }
    }

    @Override
    public ServiceButtonModel getWidgetModel() {
    ServiceButtonModel widgetModel = (ServiceButtonModel) super.getWidgetModel();
    return widgetModel;
    }

    @Override
    protected void registerPropertyChangeHandlers() {
    // The handler when PV value changed.
    IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
        @SuppressWarnings("unchecked")
        public boolean handleChange(final Object oldValue,
            final Object newValue, final IFigure figure) {
        configure(
            ((AbstractSWTWidgetFigure<ServiceButton>) getFigure())
                .getSWTWidget(),
            getWidgetModel(), ((ServiceButtonFigure) getFigure())
                .isRunMode());
        return true;
        }
    };
    setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
    // label
    IWidgetPropertyChangeHandler updateLabel = new IWidgetPropertyChangeHandler() {
        public boolean handleChange(final Object oldValue,
            final Object newValue, final IFigure figure) {
        ((AbstractSWTWidgetFigure<ServiceButton>) getFigure())
        .getSWTWidget().setLabel(getWidgetModel().getLabel());
        return true;
        }
    };
    setPropertyChangeHandler(CheckBoxModel.PROP_LABEL, updateLabel);
    }

}
