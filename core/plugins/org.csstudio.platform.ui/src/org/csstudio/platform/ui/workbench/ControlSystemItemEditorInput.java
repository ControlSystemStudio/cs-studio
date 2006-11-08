package org.csstudio.platform.ui.workbench;

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
 * @author swende
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
