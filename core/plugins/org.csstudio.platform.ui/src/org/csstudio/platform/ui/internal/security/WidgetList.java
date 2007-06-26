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
package org.csstudio.platform.ui.internal.security;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.platform.ui.security.adapter.IWidgetAdapter;

/**
 * This class contains a List of ObjectAdapterTupel and an IRight.
 * @author Kai Meyer & Torsten Witte
 */
public class WidgetList {
	
	/**
	 * The List for the ObjectAdapterTupel.
	 */
	private final List<ObjectAdapterTupel> _tupel = new LinkedList<ObjectAdapterTupel>();
	/**
	 * The default Right.
	 */
	private final String _defaultRightID;
	
	/**
	 * Constructor.
	 * @param right An IRight as default value for all contained widgets 
	 */
	public WidgetList(final String right) {
		_defaultRightID = right;
	}
	
	/**
	 * Adds the given widget to this WidgetList.
	 * @param widget  The widget, which should be added 
	 * @param adapter  The IWidgetAdapter for the given widget 
	 */
	public final void addWidget(final Object widget, final IWidgetAdapter adapter) {
		if (widget != null) {
			_tupel.add(new ObjectAdapterTupel(widget, adapter));
		}
	}
	
	/**
	 * Removes the given widget.
	 * @param widget  The widget, which sould be removed
	 * @return  True if the widget could be removed; false otherwise
	 */
	public final boolean removeWidget(final Object widget) {
		for (int i = 0; i < _tupel.size(); i++) {
			if (_tupel.get(i).getObject().equals(widget)) {
				return _tupel.remove(_tupel.get(i));
			}
		}
		return false;
	}
	
	/**
	 * Delivers the size of the internal list.
	 * @return  The size of the list
	 */
	public final int size() {
		return _tupel.size();
	}
	
	/**
	 * Checks if this WidgetList contains the given widget. 
	 * @param widget  The widget to look for 
	 * @return  True if the widget is contained; false otherwise
	 */
	public final boolean contains(final Object widget) {
		for (int i = 0; i < _tupel.size(); i++) {
			if (_tupel.get(i).getObject().equals(widget)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the list is empty.
	 * @return  True if the list is empty; false otherwise
	 */
	public final boolean isEmpty() {
		return _tupel.isEmpty();
	}
	
	/**
	 * Forces the IWidgetAdapter for the widget at an index to activate the widget.
	 * @param i  The index of the widget
	 * @param activate  The value for the activation
	 */
	public final void activate(final int i, final boolean activate) {
		if (i >= 0 && i < _tupel.size()) {
			ObjectAdapterTupel tupel = _tupel.get(i);
			tupel.getAdapter().activate(tupel.getObject(), activate);
		}
	}
	
	/**
	 * Delivers the default IRight.
	 * @return  The IRight 
	 */
	public final String getDefaultRight() {
		return _defaultRightID;
	}
}
