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

import org.csstudio.platform.ui.security.adapter.IWidgetAdapter;


/**
 * This class represents an object and its IWidgetAdapter.
 * @author Kai Meyer & Torsten Witte
 *
 */
public class ObjectAdapterTupel {
	
	/**
	 * The Object. 
	 */
	private final Object _object;
	/**
	 * The IWidgetAdapter.
	 */
	private final IWidgetAdapter _adapter;
	
	/**
	 * Constructor.
	 * @param object The widget to manage
	 * @param adapter The IWidgetAdapter to activate the widget
	 */
	public ObjectAdapterTupel(final Object object, final IWidgetAdapter adapter) {
		assert object!=null;
		_object = object;
		_adapter = adapter;
	}
	
	/**
	 * Delivers the widget.
	 * @return  The widget
	 */
	public final Object getObject() {
		return _object;
	}
	
	/**
	 * Delivers the IWidgetAdapter for the widget.
	 * @return  The IWidgetAdapter for the widget
	 */
	public final IWidgetAdapter getAdapter() {
		return _adapter;
	}
	
	/**
	 * Forces the IWidgetAdapter for the widget to activate the widget.
	 * @param activate  The value for the activation
	 */
	public final void activate(final boolean activate) {
		_adapter.activate(_object, activate);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param o another ObjectAdapterTupel
	 * @return true, if this is equal to another ObjectAdapterTupel
	 */
	public final boolean equals(final Object o) {
		if (o instanceof ObjectAdapterTupel) {
			return this.getObject().equals(((ObjectAdapterTupel)o).getObject());
		} else {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @return  the hashCode of this instance
	 */
	public final int hashCode() {
		return super.hashCode();
	}

}
