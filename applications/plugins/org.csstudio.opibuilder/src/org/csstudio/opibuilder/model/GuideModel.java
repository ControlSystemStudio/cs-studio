package org.csstudio.opibuilder.model;


 

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.util.GuideUtil;


/**
 * The Model for a Guide.
 * @author Kai Meyer
 */
public final class GuideModel implements Serializable {
	
	/**
	 * SerialVersion.
	 */
	private static final long serialVersionUID = -4503801105773940240L;
	/**
	 * Property used to notify listeners when the parts attached to a guide are changed.
	 */
	public static final String PROPERTY_CHILDREN_CHANGED = "subparts changed";
	/**
	 * Property used to notify listeners when the guide is re-positioned.
	 */
	public static final String PROPERTY_POSITION_CHANGED = "position changed";
	
	/**
	 * The Position of this guide.
	 */
	private int _position;
	/**
	 * The Orientation of this guide.
	 */
	private boolean _isHorizontal;
	/**
	 * Listeners for this guide. 
	 */
	private PropertyChangeSupport _listeners = new PropertyChangeSupport(this);
	/**
	 * A Map, which contains AbstractWidgetEditParts as keys and their alignment.
	 */
	private Map<AbstractWidgetModel, Integer> _map;
	
	/**
	 * Constructor.
	 * @param position
	 * 			The position of this guide
	 */
	public GuideModel(final int position) {
		_position = position;
	}
	
	/**
	 * Sets the orientation.
	 * @param isHorizontal
	 * 			The new orientation for this guide
	 */
	public void setOrientation(final boolean isHorizontal) {
		_isHorizontal = isHorizontal;
	}
	
	/**
	 * Sets the position.
	 * @param position
	 * 			The new Position for this guide
	 */
	public void setPosition(final int position) {
		if (_position != position) {
			int oldValue = _position;
			_position = position;
			_listeners.firePropertyChange(PROPERTY_POSITION_CHANGED, new Integer(oldValue), 
					new Integer(_position));
		}
	}
	
	/**
	 * Returns if this guide has a horizontal orientation.
	 * @return boolean
	 * 			True, if this guide has a horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return _isHorizontal;
	}
	
	/**
	 * Returns the position of this guide.
	 * @return int
	 * 			The position of this guide
	 */
	public int getPosition() {
		return _position;
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
	 * Removes a PropertyChangeListener from this guide.
	 * @param listener
	 * 			The listener to remove
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}
	
	/**
	 * Attaches the given AbstractWidgetEditPart with the alignment to this guide.
	 * @param model
	 * 			The AbstractWidgetEditPart
	 * @param alignment
	 * 			The alignment for the EditPart
	 */
	public void attachPart(final AbstractWidgetModel model, final int alignment) {
		if (getMap().containsKey(model) && getAlignment(model) == alignment) {
			return;
		}
		getMap().put(model, new Integer(alignment));
		GuideUtil.getInstance().setGuide(model, this);
		_listeners.firePropertyChange(PROPERTY_CHILDREN_CHANGED, null, model);
	}
	
	/**
	 * Detaches the given part from this guide.
	 * @param model	
	 * 			The part that is to be detached from this guide
	 */
	public void detachPart(final AbstractWidgetModel model) {
		if (getMap().containsKey(model)) {
			getMap().remove(model);
			GuideUtil.getInstance().removeGuide(model, this.isHorizontal());
//			if (this.isHorizontal()) {
//				model.setHorizontalGuide(null);
//			} else {
//				model.setVerticalGuide(null);
//			}
			_listeners.firePropertyChange(PROPERTY_CHILDREN_CHANGED, null, model);
		}
	}

	/**
	 * This methods returns the edge along which the given part is attached to this guide.
	 * This information is used by 
	 * to determine whether to attach or detach a part from a guide during resize operations.
	 * 
	 * @param	model	The part whose alignment has to be found
	 * @return	an int representing the edge along which the given part is attached to this 
	 * 			guide; 1 is bottom or right; 0, center; -1, top or left; -2 if the part is not
	 * 			attached to this guide
	 */
	public int getAlignment(final AbstractWidgetModel model) {
		if (getMap().get(model) != null) {
			return ((Integer)getMap().get(model)).intValue();
		}
		return -2;
	}

	/**
	 * @return	The Map containing all the parts attached to this guide, and their alignments;
	 * 			the keys are LogicSubparts and values are Integers
	 */
	public Map<AbstractWidgetModel, Integer> getMap() {
		if (_map == null) {
			_map = new HashMap<AbstractWidgetModel, Integer>();
		}
		return _map;
	}

	/**
	 * @return	the set of all the parts attached to this guide; a set is used because a part
	 * 			can only be attached to a guide along one edge.
	 */
	public Set<AbstractWidgetModel> getAttachedModels() {
		return getMap().keySet();
	}

}
