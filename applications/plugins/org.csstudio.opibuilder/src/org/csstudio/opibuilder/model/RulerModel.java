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
