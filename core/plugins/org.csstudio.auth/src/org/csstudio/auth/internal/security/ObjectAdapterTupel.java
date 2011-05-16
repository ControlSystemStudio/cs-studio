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

import java.lang.ref.WeakReference;

import org.csstudio.auth.security.IActivationAdapter;



/**
 * This class represents an object and its IActivationAdapter.
 * @see ActivationService for comments on weak references
 * @author Kai Meyer & Torsten Witte
 * @author Kay Kasemir Weak Reference handling
 */
public class ObjectAdapterTupel {
	
	/**
	 * Weak reference to the Object. 
	 */
	private final WeakReference<Object> _object_ref;
	
	/**
	 * The IActivationAdapter.
	 */
	private final IActivationAdapter _adapter;
	
	/**
	 * Constructor.
	 * @param object The Object to manage
	 * @param adapter The IActivationAdapter to activate the object
	 */
	public ObjectAdapterTupel(final Object object, final IActivationAdapter adapter) {
		assert object!=null;
		_object_ref = new WeakReference<Object>(object);
		_adapter = adapter;
	}
	
	/**
	 * Delivers the object.
	 * @return  The object or <code>null</code> if the object has already
	 *          been garbage collected
	 */
	public final Object getObject() {
		return _object_ref.get();
	}
	
	/**
	 * Delivers the IActivationAdapter for the object.
	 * @return  The IActivationAdapter for the object
	 */
	public final IActivationAdapter getAdapter() {
		return _adapter;
	}
	
	/**
	 * Forces the IActivationAdapter for the object to activate the object.
	 * @param activate  The value for the activation
	 */
	public final void activate(final boolean activate) {
        // Object might be garbage collected ...
        final Object object = _object_ref.get();
        if (object != null)
            _adapter.activate(object, activate);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param o another ObjectAdapterTupel
	 * @return true, if this is equal to another ObjectAdapterTupel
	 */
	@Override
    public final boolean equals(final Object o) {
		if (o instanceof ObjectAdapterTupel) {
	        // Object might be garbage collected ...
	        final Object object = _object_ref.get();
			return object != null  && object.equals(((ObjectAdapterTupel)o).getObject());
		} else {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @return  the hashCode of this instance
	 */
	@Override
    public final int hashCode() {
		return super.hashCode();
	}

}
