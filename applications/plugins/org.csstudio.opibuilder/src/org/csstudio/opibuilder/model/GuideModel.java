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
			_listeners.firePropertyChange(PROPERTY_POSITION_CHANGED, Integer.valueOf(oldValue), 
					Integer.valueOf(_position));
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
		getMap().put(model, Integer.valueOf(alignment));
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
