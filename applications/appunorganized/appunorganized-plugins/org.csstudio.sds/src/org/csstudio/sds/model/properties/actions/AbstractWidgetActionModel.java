/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.model.properties.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * A Container for the properties of an action.
 * @author Kai Meyer
 *
 */
public abstract class AbstractWidgetActionModel implements IAdaptable {
    /**
     * The used {@link WidgetProperty}s.
     */
    private Map<String, WidgetProperty> _properties;
    /**
     * The type for the {@link AbstractWidgetActionModel}.
     */
    private ActionType _type;
    /**
     * The name for the {@link AbstractWidgetActionModel}.
     */
    private String _name;
    /**
     * The internal id, used for {@link #equals(Object)}.
     */
//    private long _internalID;
    /**
     * The enabled flag of this {@link AbstractWidgetActionModel}.
     */
    private boolean _enabled = true;

    /**
     * Constructor.
     * @param name The name for the {@link AbstractWidgetActionModel}
     * @param type the type for the {@link AbstractWidgetActionModel}
     */
    public AbstractWidgetActionModel(final String name, final ActionType type) {
        assert name!=null;
        assert type!=null;
//        _internalID = System.currentTimeMillis();
        _properties = new HashMap<String, WidgetProperty>();
        _name = name;
        _type = type;
        createProperties();
    }

    /**
     * Creates the properties for the {@link AbstractWidgetActionModel}.
     */
    protected abstract void createProperties();

    /**
     * Returns a short description of the {@link AbstractWidgetActionModel}.
     * @return The short description
     */
    public abstract String getActionLabel();

    /**
     * Adds the given {@link WidgetProperty} with the given key
     * to the properties of the {@link AbstractWidgetActionModel}.
     * @param key The key for the property
     * @param property The Property
     */
    protected final void addProperty(final String key, final WidgetProperty property) {
        assert key!=null;
        assert property!=null;
        _properties.put(key, property);
    }

    /**
     * Returns a Set of the property-keys.
     * @return The Set of the property-keys
     */
    public final Set<String> getPropertyKeys() {
        return _properties.keySet();
    }

    /**
     * Return the property to the given key.
     * @param key The key of the property
     * @return The corresponding {@link WidgetProperty} or null, if the key is unknown
     */
    public final WidgetProperty getProperty(final String key) {
        return _properties.get(key);
    }

    /**
     * Returns the used properties.
     * @return The properties
     */
    public final Collection<WidgetProperty> getProperties() {
        return _properties.values();
    }

    /**
     * Checks if the {@link AbstractWidgetActionModel} has a property with the given key.
     * @param key The key of the property
     * @return true, if a property with the key exists, false otherwise
     */
    public final boolean hasProperty(final String key) {
        return this.getPropertyKeys().contains(key);
    }

    /**
     * Creates a new {@link AbstractWidgetActionModel} with the properties and values of the original.
     * @return The copy
     */
    public final AbstractWidgetActionModel makeCopy() {
        AbstractWidgetActionModel newAction = this.getType().getActionFactory().createWidgetActionModel();
        for (String key : this.getPropertyKeys()) {
            newAction.getProperty(key).setPropertyValue(this.getProperty(key).getPropertyValue());
        }
        return newAction;
    }

    /**
     * Return the name of the {@link AbstractWidgetActionModel}.
     * @return The name of the {@link AbstractWidgetActionModel}
     */
    public final String getName() {
        return _name;
    }

    /**
     * Return the type of the {@link AbstractWidgetActionModel}.
     * @return The type of the {@link AbstractWidgetActionModel}
     */
    public final ActionType getType() {
        return _type;
    }

    /**
     * Returns if this {@link AbstractWidgetActionModel} is enabled.
     * @return True if this {@link AbstractWidgetActionModel} is enabled, false otherwise
     */
    public final boolean isEnabled() {
        return _enabled;
    }

    /**
     * Sets if this {@link AbstractWidgetActionModel} is enabled or not.
     * @param enabled True if this {@link AbstractWidgetActionModel} should be enabled, false otherwise
     */
    public final void setEnabled(final boolean enabled) {
        _enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public final Object getAdapter(final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
//        int hashCode = Long.valueOf(_internalID).hashCode();
//        hashCode = hashCode + this.getType().hashCode() * prime;
        int hashCode = this.getType().hashCode() * prime;
        for (String key : this.getPropertyKeys()) {
            Object value = this.getProperty(key).getPropertyValue();
            hashCode = hashCode + key.hashCode() * prime;
            hashCode = hashCode + value.hashCode() * prime;
        }
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object arg) {
        if (arg instanceof AbstractWidgetActionModel) {
            AbstractWidgetActionModel that = (AbstractWidgetActionModel) arg;
//            if (this.getType().equals(that.getType()) && this._internalID==that._internalID) {
            if (this.getType().equals(that.getType())) {
                for (String key : this.getPropertyKeys()) {
                    if (that.hasProperty(key)) {
                        Object thisValue = this.getProperty(key).getPropertyValue();
                        Object thatValue = that.getProperty(key).getPropertyValue();
                        if (!thisValue.equals(thatValue)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
