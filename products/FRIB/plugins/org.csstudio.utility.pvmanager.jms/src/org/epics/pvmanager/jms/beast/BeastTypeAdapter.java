/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms.beast;

import javax.jms.Message;
import org.epics.pvmanager.DataSourceTypeAdapter;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.jms.JMSChannelHandler;

public abstract class BeastTypeAdapter implements DataSourceTypeAdapter<BeastChannelHandler, Message> {

    // e.g. VDouble.class
    private final Class<?> typeClass;
    private final Class<?> messageClass;

    /**
     * Creates a new type adapter.
     *
     * @param typeClass the java type this adapter will create
     * @param message <code>Message</code> instance this adapter will convert
     */
    public BeastTypeAdapter(Class<?> typeClass, Class<?> messageClass) {
        this.typeClass = typeClass;
        this.messageClass = messageClass;
    }

    @Override
    public int match(ValueCache<?> cache, BeastChannelHandler channel) {

        // If the generated type can't be put in the cache, no match
        if (!cache.getType().isAssignableFrom(typeClass)) {
            return 0;
        }

        // Everything matches
        return 1;
    }

    @Override
    public Object getSubscriptionParameter(ValueCache<?> cache, BeastChannelHandler channel) {
        throw new UnsupportedOperationException("Not implemented: JMSChannelHandler is multiplexed, will not use this method");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean updateCache(ValueCache cache, BeastChannelHandler beastChannelHandler, Message message) {
        Object value = createValue(message, !beastChannelHandler.isConnected());
        cache.writeValue(value);
        return true;
    }

    /**
     * Given the value and the (optional) metadata, will create the new value.
     *
     * @param value the value taken from the monitor
     * @param metadata the value taken as metadata
     * @param disconnected true if the value should report the channel is
     * currently disconnected
     * @return the new value
     */
    public abstract Object createValue(Message message, boolean disconnected);
}

