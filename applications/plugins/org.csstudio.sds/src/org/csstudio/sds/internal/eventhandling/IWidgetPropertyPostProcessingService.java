package org.csstudio.sds.internal.eventhandling;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Service that uses extension contributions for extension point
 * {@link SdsPlugin#EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS} to apply further
 * changes to widget models after property changes.
 *
 * @author Sven Wende
 *
 */
public interface IWidgetPropertyPostProcessingService {

    /**
     * Applies contributed post-processors for all properties of the widget.
     * Usually this method should be called during model creation or loading.
     *
     * Important: Changes are executed within this method. No further action is
     * necessary.
     *
     * @param widget
     *            the widget that should get initialized
     * @param eventType
     *            the type of event (used to distinguish between loading
     *            operation or manual changes in the display editor)
     */
    void applyForAllProperties(AbstractWidgetModel widget, EventType eventType);

    /**
     * Applies contributed post-processors for a single widget property. Usually
     * this method should be called when a single property of a widget is
     * changed using the property view.
     *
     * Important: Changes are NOT directly executed within this method. Instead
     * a given command chain is equipped with the necessary commands. So - the
     * command chain has to be executed afterwards to cause any effects.
     *
     * @param widget
     *            the widget
     * @param widgetProperty
     *            the widget
     * @param chain
     * @param eventType
     *            the type of event (used to distinguish between loading
     *            operation or manual changes in the display editor)
     */
    void applyForSingleProperty(AbstractWidgetModel widget, WidgetProperty widgetProperty, CompoundCommand chain, EventType eventType);

}