package org.csstudio.opibuilder.widgets.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;

import static org.epics.pvmanager.ExpressionLanguage.*;

public class VTableDisplayEditPart extends AbstractSelectionWidgetEditpart<VTableDisplayFigure, VTableDisplayModel> {

    /**
     * Create and initialize figure.
     */
    @Override
    protected VTableDisplayFigure doCreateFigure() {
        VTableDisplayFigure figure = new VTableDisplayFigure(this);
        configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
        return figure;
    }

    private static void configure(final VTableWidget widget, VTableDisplayModel model,
            boolean runMode) {
        if (runMode) {
            widget.setPvFormula(model.getPvFormula());
            if (model.getSelectionPv() != null && !model.getSelectionPv().trim().isEmpty()) {
                final PVWriter<Object> pvWriter = PVManager.write(channel(model.getSelectionPv()))
                        .async();
                widget.addDisposeListener(new DisposeListener() {

                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        pvWriter.close();
                    }
                });
                widget.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("selectionValue".equals(evt.getPropertyName())) {
                            pvWriter.write(widget.getSelectionValue());
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        // The handler when PV value changed.
        IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                configure(getFigure().getSWTWidget(), getWidgetModel(),
                        getFigure().isRunMode());
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
        registerCommonProperties();
    }


}
