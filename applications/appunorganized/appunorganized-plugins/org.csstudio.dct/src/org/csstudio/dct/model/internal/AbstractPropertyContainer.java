package org.csstudio.dct.model.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IPropertyContainer;

/**
 * Standard implementation of {@link IPropertyContainer}.
 *
 * @author Sven Wende
 */
public abstract class AbstractPropertyContainer extends AbstractElement implements IPropertyContainer {

    private static final long serialVersionUID = 6872019128384162503L;
    private Map<String, String> properties;

    public AbstractPropertyContainer() {
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param id
     *            the id
     */
    public AbstractPropertyContainer(String name, UUID id) {
        super(name, id);
        properties = new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeProperty(String key) {
        properties.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof AbstractPropertyContainer) {
            AbstractPropertyContainer container = (AbstractPropertyContainer) obj;

            // .. super
            if (super.equals(obj)) {
                // .. properties
                if (getProperties().equals(container.getProperties())) {
                    result = true;
                }
            }
        }

        return result;
    }
}
