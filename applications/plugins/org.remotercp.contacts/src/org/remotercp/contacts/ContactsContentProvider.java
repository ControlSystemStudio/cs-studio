package org.remotercp.contacts;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ContactsContentProvider implements ITreeContentProvider {
	protected IWorkbenchAdapter getAdapter(Object element) {
		IWorkbenchAdapter adapter = null;
		if (element instanceof IAdaptable)
			adapter = (IWorkbenchAdapter) ((IAdaptable) element)
					.getAdapter(IWorkbenchAdapter.class);
		if (element != null && adapter == null)
			adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
					.loadAdapter(element, IWorkbenchAdapter.class.getName());
		return adapter;
	}

	public Object[] getChildren(Object element) {

		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null) {
			return adapter.getChildren(element);
		}
		return new Object[0];
	}

	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	public Object getParent(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null) {
			return adapter.getParent(element);
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
}
