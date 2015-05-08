package org.csstudio.sds.eventhandling;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.MetaData;

/**
 * Base class for behaviors. A behavior encapsulates code that influences a
 * widgets appearance when it is connected to a control system. Behavior
 * implementations have to be stateless - at runtime a single behavior instance
 * is used to update all widgets that use this behavior.
 *
 * Behaviors are registered as an extension to
 * {@link SdsPlugin#EXTPOINT_BEHAVIORS}.
 *
 * @author Kai Meyer, Sven Wende
 *
 * @param <W>
 *            the type of widget which is controlled by this behavior
 */
public abstract class AbstractBehavior<W extends AbstractWidgetModel> {
    private static final String PROP_DESCRIPTION = "description";
    private static final String PROP_WIDGET_TYPE_ID = "widgetTypeId";
    private static final String PROP_ID = "id";

    private String _behaviorId;
    private String _widgetTypeId;
    private String _description;

    public final void setInitializationData(final IConfigurationElement config,
                                            final String propertyName,
                                            final Object data) throws CoreException {
        _behaviorId = config.getAttribute(PROP_ID);
        _widgetTypeId = config.getAttribute(PROP_WIDGET_TYPE_ID);
        _description = config.getAttribute(PROP_DESCRIPTION);
        assert _behaviorId != null : "behaviorId != null";
        assert _behaviorId.trim().length() > 0 : "behaviorId.trim().length() > 0";
        assert _widgetTypeId != null : "widgetTypeId != null";
        assert _widgetTypeId.trim().length() > 0 : "widgetTypeId.trim().length() > 0";
        assert _description != null : "description != null";
        assert _description.trim().length() > 0 : "description.trim().length() > 0";
    }

    public final String getBehaviorId() {
        return _behaviorId;
    }

    public final String getDescription() {
        return _description;
    }

    /**
     * Returns the widget type, this behavior is registered for.
     *
     * @return the widget type, this behavior is registered for
     */
    public final String getWidgetTypeId() {
        return _widgetTypeId;
    }

    /**
     * Initializes the widget independently of any connections. Will be called
     * before the widget is connected to the control system.
     *
     *
     * @param widget
     *            the widget
     */
    public final void initializeWidget(final W widget) {
        assert widget != null;
        doInitialize(widget);
    }

    /**
     * Handles a change of the {@link AnyData} object which provides some
     * metadata for the current connection.
     *
     * @param model
     *            the widget
     * @param anyData
     *            the {@link AnyData} object
     */

    /**
     * Processes DAL events received via
     * {@link ChannelListener#channelDataUpdate(AnyDataChannel)}. By default
     * this method delegates event handling to different template methods
     * {@link #doProcessMetaDataChange(AbstractWidgetModel, MetaData)} ,
     * {@link #doProcessValueChange(AbstractWidgetModel, AnyData)} and
     * {@link #doProcessConnectionStateChange(AbstractWidgetModel, AnyDataChannel)}
     * hiding some of the flexibility that comes with {@link AnyDataChannel}.
     *
     * Subclasses that need to access all features offered by
     * {@link AnyDataChannel} may override this method.
     *
     * @param model
     *            the widget model that needs to be changed
     * @param channel
     *            the {@link AnyDataChannel}
     */
    public final void processChannelDataUpdate(final W model, final AnyDataChannel channel) {
        AnyData data = channel.getData();

        doProcessMetaDataChange(model, data.getMetaData());
        doProcessValueChange(model, data);
        doProcessConnectionStateChange(model, channel);
    }

    /**
     * Processes DAL events received via
     * {@link ChannelListener#channelDataUpdate(AnyDataChannel)}. By default
     * this method delegates event handling to
     * {@link #doProcessConnectionStateChange(AbstractWidgetModel, AnyDataChannel)}
     * hiding some of the flexibility that comes with {@link AnyDataChannel}.
     *
     * Subclasses that need to access all features offered by
     * {@link AnyDataChannel} may override this method.
     *
     * @param model
     *            the widget model that needs to be changed
     * @param channel
     *            the {@link AnyDataChannel}
     */
    public final void processChannelStateUpdate(final W model, final AnyDataChannel channel) {
        doProcessConnectionStateChange(model, channel);
    }

    /**
     * Handles changes of widget property manual values.
     * @param widgetModel
     *               the model of the current widget
     * @param propertyId
     *            the property id
     * @param value
     *            the new manual value
     */
    public final Object convertOutgoingValue(final W widgetModel,
                                             final String propertyId,
                                             final Object value) {
        assert propertyId != null;
        return doConvertOutgoingValue(widgetModel, propertyId, value);
    }

    /**
     * Returns ids of properties that will become invisible when this behavior
     * is active.
     *
     * @return ids of properties that will become invisible when this behavior
     *         is active
     */
    public final String[] getInvisiblePropertyIds() {
        String[] result = doGetInvisiblePropertyIds();

        if (result == null) {
            return new String[0];
        }
        return result;
    }

    /**
     * Returns ids of properties that are used to trigger write access to the
     * control system.
     *
     * @return ids of properties that are used to trigger write access
     */
    public final String[] getSettablePropertyIds() {
        String[] result = doGetSettablePropertyIds();

        if (result == null) {
            result = new String[0];
        }

        return result;
    }

    /**
     * Template method which is called when a manual value of a widget property
     * changes. Subclasses should apply type conversion if necessary. By default
     * the current value is returned without any conversion.
     * @param widgetModel
     *               the model of the current widget
     * @param propertyId
     *            the property id
     * @param value
     *            the new manual value for that property
     */
    protected Object doConvertOutgoingValue(final W widgetModel,
                                            final String propertyId,
                                            final Object value) {
        return value;
    }

    /**
     * Template method which should return the ids of properties that are used
     * for writing values to the control system. Returns an empty set by
     * default. May be overridden by subclasses.
     *
     * @return ids of writable properties or null
     */
    protected String[] doGetSettablePropertyIds() {
        return new String[0];
    }

    /**
     * Template method which is called before the widget is connected to the
     * control systems. Subclasses should initialize the widgets initial look
     * and feel.
     *
     * @param widget
     *            the widget
     */
    protected abstract void doInitialize(W widget);

    /**
     * Template method which is called when the connection state of the
     * underlying channel changes. Subclasses may implement a widgets look and
     * feel depending on the current connection state, e.g. configure a red
     * border in case of a {@link ConnectionState#CONNECTION_LOST} state.
     *
     * @param widget
     *            the widget
     * @param anyDataChannel
     *            the current connection state
     */
    protected abstract void doProcessConnectionStateChange(W widget, AnyDataChannel anyDataChannel);

    /**
     * Template method which is called when the value of the underlying channel
     * changes. Subclasses may implement a widgets look and feel depending on
     * the current value.
     *
     * @param widget
     *            the widget
     * @param connectionState
     *            the current connection state
     */
    protected abstract void doProcessValueChange(W model, AnyData anyData);

    /**
     * Template method which is called when the {@link AnyData} of the
     * underlying channel changes. Subclasses may implement a widgets look and
     * feel depending on the current {@link AnyData} and its meta data.
     *
     * @param widget
     *            the widget
     * @param metaData
     *            the {@link MetaData}
     */
    protected abstract void doProcessMetaDataChange(W widget, MetaData metaData);

    /**
     * Subclasses should return identifiers of all properties handled by this
     * behavior. Those properties will not appear in the property view when this
     * behavior is active.
     *
     * @return identifiers of all properties handled by this behavior
     */
    protected abstract String[] doGetInvisiblePropertyIds();

    /**
     *{@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("Behavior (id: ");
        buffer.append(_behaviorId);
        buffer.append(", widgetTypeId: ");
        buffer.append(_widgetTypeId);
        buffer.append(", description: ");
        buffer.append(_description);
        return buffer.toString();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (_behaviorId == null) ? 0 : _behaviorId.hashCode());
        result = prime * result + ( (_widgetTypeId == null) ? 0 : _widgetTypeId.hashCode());
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractBehavior<W> other = (AbstractBehavior<W>) obj;
        if (_behaviorId == null) {
            if (other._behaviorId != null) {
                return false;
            }
        } else if (!_behaviorId.equals(other._behaviorId)) {
            return false;
        }
        if (_widgetTypeId == null) {
            if (other._widgetTypeId != null) {
                return false;
            }
        } else if (!_widgetTypeId.equals(other._widgetTypeId)) {
            return false;
        }
        return true;
    }

}
