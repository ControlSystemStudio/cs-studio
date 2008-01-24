package org.csstudio.sds.model.layers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
		_id = id;
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
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		if (obj instanceof Layer) {
			String id = ((Layer) obj)._id;
			result = id.equals(_id);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return _id.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _description;
	}

}
