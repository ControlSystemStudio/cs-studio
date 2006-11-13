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
package org.csstudio.platform.ui.workbench;

import org.csstudio.platform.ui.internal.workbench.FileEditorInputFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

/**
 * Adapter for making a file resource a suitable input for an editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @author swende
 * @version $Revision$
 */
public final class FileEditorInput implements IEditorInput, IAdaptable,
		IPersistableElement {
	/**
	 * A file.
	 */
	private IFile _file;

	/**
	 * Creates an editor input based of the given file resource.
	 * 
	 * @param file
	 *            the file resource
	 */
	public FileEditorInput(final IFile file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		_file = file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return _file.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FileEditorInput)) {
			return false;
		}
		FileEditorInput other = (FileEditorInput) obj;
		return _file.equals(other.getFile());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean exists() {
		boolean result = false;
		Object o = getAdapter(IResource.class);

		if (o != null) {
			IResource res = (IResource) o;
			result = res.exists();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		return _file.getAdapter(adapter);
	}

	/**
	 * Gets the workspace file.
	 * 
	 * @return IFile the workspace file
	 */
	public IFile getFile() {
		return _file;
	}

	/**
	 * {@inheritDoc}
	 */
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor result = null;
		Object o = getAdapter(IResource.class);

		if (o != null) {
			IResource res = (IResource) o;
			result = PlatformUI.getWorkbench().getEditorRegistry()
					.getImageDescriptor(res.getName());
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getToolTipText() {
		String result = ""; //$NON-NLS-1$
		Object o = getAdapter(IResource.class);

		if (o != null) {
			IResource res = (IResource) o;
			result = res.getFullPath().makeRelative().toString();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		String result = null;
		Object o = getAdapter(IResource.class);

		if (o != null) {
			IResource res = (IResource) o;
			result = res.getName();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveState(final IMemento memento) {
		FileEditorInputFactory.saveState(memento, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFactoryId() {
		return FileEditorInputFactory.ID;
	}
}
