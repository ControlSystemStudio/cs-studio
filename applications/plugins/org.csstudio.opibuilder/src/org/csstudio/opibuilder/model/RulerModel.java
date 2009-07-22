package org.csstudio.opibuilder.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * The model for a Ruler.
 * @author Kai Meyer
 *
 */
public final class RulerModel implements Serializable {
	
	/**
	 * SerialVersion.
	 */
	private static final long serialVersionUID = -5738445947935719586L;

	/**
	 * The ID for the children changed property.
	 */
	public static final String PROPERTY_CHILDREN_CHANGED = "PROPERTY_CHILDREN_CHANGED";
	/**
	 * The guides of this ruler.
	 */
	private List<GuideModel> _guides = new LinkedList<GuideModel>();
	/**
	 * The orientation of this ruler.
	 */
	private boolean _isHorizontal;
	/**
	 * The PropertyChangeListeners for this ruler.
	 */
	private PropertyChangeSupport _listeners = new PropertyChangeSupport(this);
	
	/**
	 * Constructor.
	 * @param isHorizontal
	 * 			The orientation of this ruler
	 */
	public RulerModel(final boolean isHorizontal) {
		_isHorizontal = isHorizontal;
	}
	
	/**
	 * Adds the given guide to this ruler.
	 * Notifies all registered listeners
	 * @param guide
	 * 			The guide to add
	 */
	public void addGuide(final GuideModel guide) {
		if (!_guides.contains(guide)) {
			guide.setOrientation(!_isHorizontal);
			_guides.add(guide);
			_listeners.firePropertyChange(PROPERTY_CHILDREN_CHANGED, null, guide);
		}
	}
	
	/**
	 * Removes the given guide from this ruler.
	 * Notifies all registered listeners
	 * @param guide
	 * 			The guide to remove
	 */
	public void removeGuide(final GuideModel guide) {
		if (_guides.remove(guide)) {
			_listeners.firePropertyChange(PROPERTY_CHILDREN_CHANGED, null, guide);
		}
	}
	
	/**
	 * Adds a PropertyChangeListener to this guide.
	 * @param listener
	 * 			The listener to add
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes the PropertyChangeListener from this guide.
	 * @param listener
	 * 			The listener to remove
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}
	
	/**
	 * Returns a List of all guides, contained by this ruler.
	 * @return List
	 * 			A List of GuideModels
	 */
	public List<GuideModel> getGuides() {
		return _guides;
	}
	
	/**
	 * Returns if this guide has a horizontal orientation.
	 * @return boolean
	 * 			True, if this guide has a horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return _isHorizontal;
	}

}
