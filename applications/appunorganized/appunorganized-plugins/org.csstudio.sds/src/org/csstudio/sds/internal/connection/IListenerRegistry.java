package org.csstudio.sds.internal.connection;

import org.csstudio.sds.model.IPropertyChangeListener;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.EditPart;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;

/**
 * A listener registry maintains listeners that have been created for connecting
 * widgets to the control system. This interface should only be implemented by
 * {@link EditPart}s which are the controllers for widgets.
 *
 * The listener registry is also responsible for disposing/removing all
 * listeners when the display gets closed.
 *
 * @author Sven Wende
 *
 */
public interface IListenerRegistry {
    /**
     * Registers the specified {@link ChannelListener}. The listener will be
     * connected to the control system.
     *
     * @param parameters
     *            the connection parameters
     * @param listener
     *            the listener
     */
    void register(ConnectionParameters parameters, ChannelListener listener);

    /**
     * Registers the specified {@link IPropertyChangeListener}. The listener
     * will be connected to the specified {@link WidgetProperty}.
     *
     * @param property the property
     * @param listener the listener
     */
    void register(WidgetProperty property, IPropertyChangeListener listener);
}
