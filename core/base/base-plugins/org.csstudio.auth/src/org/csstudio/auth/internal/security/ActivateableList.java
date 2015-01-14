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
package org.csstudio.auth.internal.security;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.auth.security.IActivationAdapter;

/**
 * This class contains a List of ObjectAdapterTupel and an IRight.
 * 
 * All the objects referenced from this list use weak links to allow
 * them to be garbage collected.
 * This means that all code in here must handle the case of object references
 * turning to <code>null</code>.
 * 
 * The list 'shrinks' to remove references to garbage-collected objects
 * in <code>activate()</code>
 *
 * @see ActivationService for more comments on weak references
 * @author Kai Meyer & Torsten Witte
 * @author Kay Kasemir Weak Reference handling
 */
public class ActivateableList {
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
	 * @param right An IRight as default value for all contained objects 
	 */
	public ActivateableList(final String right) {
		_defaultRightID = right;
	}
	
	/**
	 * Adds the given object to this ActivateabletList.
	 * @param object  The object, which should be added 
	 * @param adapter  The IActivationAdapter for the given object 
	 */
	public final void addObject(final Object object, final IActivationAdapter adapter) {
		if (object != null) {
			_tupel.add(new ObjectAdapterTupel(object, adapter));
		}
	}
	
	/**
	 * Removes the given object.
	 * @param object  The object, which should be removed
	 * @return  True if the object could be removed; false otherwise
	 */
	public final boolean removeObject(final Object object) {
	    if (object == null)
	        return false;
	    // object is non-null, but tupel's object references might be!
		for (int i = 0; i < _tupel.size(); i++)
			if (object.equals(_tupel.get(i).getObject()))
				return _tupel.remove(_tupel.get(i));
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
	 * Checks if this ActivateableList contains the given object. 
	 * @param object  The object to look for 
	 * @return  True if the object is contained; false otherwise
	 */
	public final boolean contains(final Object object) {
	    if (object == null)
	        return false;
        // object is non-null, but tupel's object references might be!
		for (int i = 0; i < _tupel.size(); i++)
			if (object.equals(_tupel.get(i).getObject()))
				return true;
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
	 * Invoke the IActivationAdapter for all objects on the list
	 * @param activate  The value for the activation
	 */
	public final void activate(final boolean activate)
	{
	    int i = 0;
	    while (i < _tupel.size())
	    {
	        final ObjectAdapterTupel tupel = _tupel.get(i);
			final Object object = tupel.getObject();
			if (object == null)
			{   // Referenced object has been garbage collected,
			    // remove from list
			    _tupel.remove(i);
			}
			else
			{   // Update activation state of object, move to next list item
                tupel.getAdapter().activate(object, activate);
                ++i;
			}
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
