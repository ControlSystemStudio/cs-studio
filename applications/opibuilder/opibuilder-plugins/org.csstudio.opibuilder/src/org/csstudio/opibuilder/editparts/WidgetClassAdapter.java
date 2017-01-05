/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.opibuilder.editparts;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.BOYPVFactory;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * <code>WidgetClassAdapter</code> takes care of changing the model according to the widget class value. It registers
 * property listeners on the widget_class and widget_class_value properties and whenever there is a change it updates
 * the model accordingly.
 *
 * @see SchemaService#applyWidgetClassProperties(AbstractWidgetModel)
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WidgetClassAdapter {

    private IPVListener widgetClassListener = new IPVListener.Stub() {
        @Override
        public void valueChanged(IPV pv) {
            VType type = pv.getValue();
            if (type instanceof VString) {
                ((AbstractWidgetModel) editPart.getModel()).getProperty(AbstractWidgetModel.PROP_WIDGET_CLASS_VALUE)
                    .setPropertyValue(((VString) type).getValue());
            }
        }
    };

    private Optional<IPV> widgetClassPV = Optional.empty();
    private final AbstractGraphicalEditPart editPart;

    /**
     * Construct a new widget class adapter for the given edit part. The edit part is expected to provide
     * {@link AbstractWidgetModel} when {@link AbstractGraphicalEditPart#getModel()} is invoked.
     *
     * @param editPart the edit part
     */
    public WidgetClassAdapter(AbstractGraphicalEditPart editPart) {
        this.editPart = editPart;
    }

    /**
     * Deactivate the adapter, by releasing the PV responsible for applying the class value.
     */
    public void deactivate() {
        widgetClassPV.ifPresent(pv -> {
            pv.stop();
            pv.removeListener(widgetClassListener);
        });
    }

    /**
     * Setup this adapter by registering for the necessary property change events.
     */
    public void setUp() {
        final AbstractWidgetModel model = (AbstractWidgetModel) editPart.getModel();
        if (model != null) {
            if (editPart instanceof AbstractBaseEditPart) {
                ((AbstractBaseEditPart) editPart).setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDGET_CLASS_VALUE,
                    (oldValue, newValue, figure) -> {
                        SchemaService.getInstance().applyWidgetClassProperties(model);
                        ((AbstractBaseEditPart) editPart).refreshVisuals();
                        return true;
                    });
                ((AbstractBaseEditPart) editPart).setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDGET_CLASS,
                    (o, n, f) -> setUpWidgetClass((String) n));
                setUpWidgetClass((String) model.getPropertyValue(AbstractWidgetModel.PROP_WIDGET_CLASS));
            } else {
                model.getProperty(AbstractWidgetModel.PROP_WIDGET_CLASS_VALUE)
                    .addPropertyChangeListener(evt -> editPart.getViewer().getControl().getDisplay()
                        .asyncExec(() -> SchemaService.getInstance().applyWidgetClassProperties(model)));
                model.getProperty(AbstractWidgetModel.PROP_WIDGET_CLASS)
                    .addPropertyChangeListener(evt -> setUpWidgetClass((String) evt.getNewValue()));
                setUpWidgetClass((String) model.getPropertyValue(AbstractWidgetModel.PROP_WIDGET_CLASS));
            }
        }
    }

    private boolean setUpWidgetClass(String widgetClass) {
        widgetClassPV.ifPresent(pv -> {
            pv.stop();
            pv.removeListener(widgetClassListener);
        });
        try {
            if (widgetClass != null && !widgetClass.trim().isEmpty()) {
                AbstractWidgetModel model = (AbstractWidgetModel) editPart.getModel();
                // set the widget class, just in case that the given class is not a pv, but a real class value
                model.getProperty(AbstractWidgetModel.PROP_WIDGET_CLASS_VALUE).setPropertyValue(widgetClass);
                widgetClassPV = Optional.of(BOYPVFactory.createPV(widgetClass, false, 2));
                widgetClassPV.get().addListener(widgetClassListener);
                widgetClassPV.get().start();
            }
        } catch (Exception e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Unable to connect to PV:" + widgetClass, e); //$NON-NLS-1$
        }
        return false;
    }
}
