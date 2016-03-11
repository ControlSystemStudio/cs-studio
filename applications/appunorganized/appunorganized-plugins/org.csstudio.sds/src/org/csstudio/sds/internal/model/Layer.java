/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.internal.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Model representation of a layer.
 *
 * Layers have the following attributes:
 *
 * <ul>
 * <li>ID</li>
 * <li>description</li>
 * <li>visibility</li>
 * </ul>
 *
 * The <i>ID</i> attribute is used for equality checks only, while the
 * <i>description</i> is optional.
 *
 * @author swende
 *
 */
public final class Layer implements IAdaptable {
    /**
     * ID for <i>visibility changed</i> events.
     */
    public static final String PROP_VISIBLE = "visible";

    /**
     * ID for <i>id changed</i> events.
     */
    public static final String PROP_ID = "id";

    /**
     * ID for <i>description changed</i> events.
     */
    public static final String PROP_DESCRIPTION = "description";

    /**
     * Property change support delegate.
     */
    private PropertyChangeSupport _propertyChangeSupport;

    /**
     * The visibility state.
     */
    private boolean _visible;

    /**
     * The layer id.
     */
    private String _id;

    /**
     * A layer description.
     */
    private String _description;

    /**
     * Constructor.
     *
     * @param id
     *            the layer ID
     * @param description
     *            a description for the layer
     */
    public Layer(final String id, final String description) {
        if (id != null && id.trim().length() > 0) {
            _id = id;
        } else {
            _id = UUID.randomUUID().toString();
        }
        _description = description;
        _visible = true;
        _propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Returns the layer description.
     *
     * @return the layer description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the layer description.
     *
     * @param newDescription
     *            the layer description
     */
    public void setDescription(final String newDescription) {
        assert newDescription != null;

        if (!newDescription.equals(_description)) {
            String oldDescription = _description;
            _description = newDescription;
            _propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION,
                    oldDescription, newDescription);
        }
    }

    /**
     * Returns the layer ID.
     *
     * @return the layer ID
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the layer ID.
     *
     * @param newId
     *            the layer ID
     */
    public void setId(final String newId) {
        assert newId != null;

        if (!newId.equals(_id)) {
            String oldId = _id;
            _id = newId;
            _propertyChangeSupport.firePropertyChange(PROP_ID, oldId, newId);
        }
    }

    /**
     * Returns the visibility state.
     *
     * @return the visibility state
     */
    public boolean isVisible() {
        return _visible;
    }

    /**
     * Sets the visibility state.
     *
     * @param visible
     *            the visibility state
     */
    public void setVisible(final boolean visible) {
        if (_visible != visible) {
            _visible = visible;
            _propertyChangeSupport.firePropertyChange(PROP_VISIBLE, !visible,
                    visible);
        }
    }

    /**
     * Adds the specified property change listener.
     *
     * @param listener
     *            a property change listener
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        _propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes the specified property change listener.
     *
     * @param listener
     *            a property change listener
     */
    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        _propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_description == null) ? 0 : _description.hashCode());
        result = prime * result + ((_id == null) ? 0 : _id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Layer other = (Layer) obj;
        if (_description == null) {
            if (other._description != null)
                return false;
        } else if (!_description.equals(other._description))
            return false;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _description + " [" + _id + "]";
    }

}
