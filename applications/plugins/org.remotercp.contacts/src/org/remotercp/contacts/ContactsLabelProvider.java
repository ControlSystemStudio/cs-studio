package org.remotercp.contacts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ContactsLabelProvider extends LabelProvider {

	private Map<ImageDescriptor, Image> imageTable = new HashMap<ImageDescriptor, Image>(
			7);

	// private IRosterItem selection;

	protected final IWorkbenchAdapter getAdapter(Object element) {
		IWorkbenchAdapter adapter = null;
		if (element instanceof IAdaptable)
			adapter = (IWorkbenchAdapter) ((IAdaptable) element)
					.getAdapter(IWorkbenchAdapter.class);
		if (adapter == null)
			adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
					.loadAdapter(element, IWorkbenchAdapter.class.getName());
		return adapter;
	}

	public final Image getImage(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null) {
			return null;
		}
		ImageDescriptor descriptor = adapter.getImageDescriptor(element);
		if (descriptor == null) {
			return null;
		}
		Image image = (Image) imageTable.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageTable.put(descriptor, image);
		}
		return image;
	}

	public final String getText(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null) {
			return "";
		}
		return adapter.getLabel(element);
	}

	public void dispose() {
		if (imageTable != null) {
			for (Iterator<Image> i = imageTable.values().iterator(); i
					.hasNext();) {
				i.next().dispose();
			}
			imageTable = null;
		}
	}

	public Color getBackground(Object element) {
		return null;
	}
}
