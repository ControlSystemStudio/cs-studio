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
package org.csstudio.auth.ui.workbench;

import org.csstudio.platform.model.IControlSystemItem;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Adapter for making a platform object a suitable input for an editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ControlSystemItemEditorInput implements IEditorInput,
		IAdaptable {
	/**
	 * A platform object.
	 */
	private IControlSystemItem _cssPlatformObject;

	/**
	 * Creates an editor input based of the given platform object.
	 * 
	 * @param cssPlatformObject
	 *            the platform object
	 */
	public ControlSystemItemEditorInput(
			final IControlSystemItem cssPlatformObject) {
		if (cssPlatformObject == null) {
			throw new IllegalArgumentException();
		}
		_cssPlatformObject = cssPlatformObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return _cssPlatformObject.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		if (obj instanceof ControlSystemItemEditorInput) {
			result = _cssPlatformObject
					.equals(((ControlSystemItemEditorInput) obj)
							.getCssPlatformObject());
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		return _cssPlatformObject.getAdapter(adapter);
	}

	/**
	 * {@inheritDoc}
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getToolTipText() {
		return _cssPlatformObject.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return _cssPlatformObject.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Gets the platform object.
	 * 
	 * @return the platform object
	 */
	public IControlSystemItem getCssPlatformObject() {
		return _cssPlatformObject;
	}

}
