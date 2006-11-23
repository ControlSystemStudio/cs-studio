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
package org.csstudio.platform.model;

import org.csstudio.platform.util.ControlSystemItemPath;

/**
 * This class defines the callback interface for the extension point
 * <b>org.csstudio.platform.controlSystemItemFactories</b>. Please refer to the
 * documentation of that extension point for further details.
 * 
 * A control system item factory, is responsible for the creation of a certain
 * type of control system items. These items have to implement
 * {@link IControlSystemItem}.
 * 
 * @author Sven Wende
 * 
 * @param <T>
 *            a type parameter
 */
public abstract class AbstractControlSystemItemFactory<T extends IControlSystemItem> {

	/**
	 * Creates a String representation for the specified control system item.
	 * This method corresponds to
	 * {@link #createItemFromStringRepresentation(String)} and should be coded
	 * with the same String representation in mind.
	 * 
	 * @param item
	 *            the control system item
	 * @return a String representation of the item
	 */
	protected abstract String createStringRepresentationFromItem(T item);

	/**
	 * Creates a control system item from the specified String representation.
	 * This method corresponds to
	 * {@link #createStringRepresentationFromItem(IControlSystemItem)} and
	 * should be coded with the same String representation in mind.
	 * 
	 * @param string
	 *            a string representation
	 * @return a control system item or null, if none could be obtained from the
	 *         specified String representation
	 */
	protected abstract T createItemFromStringRepresentation(String string);

	/**
	 * Creates a control system item for the specified path.
	 * 
	 * @param path
	 *            a path
	 * 
	 * @return a control system item or null, if none could be obtained from the
	 *         specified path
	 */
	public final T createControlSystemItem(final ControlSystemItemPath path) {
		assert path != null;
		return createItemFromStringRepresentation(path.getItemData());
	}

	/**
	 * Creates a path for the specified control system item.
	 * 
	 * @param item
	 *            a control system item
	 * @return the corresponding path
	 */
	public final ControlSystemItemPath getTransportablePath(final T item) {
		assert item != null;
		return new ControlSystemItemPath(item.getTypeId(),
				createStringRepresentationFromItem(item));
	}
}
