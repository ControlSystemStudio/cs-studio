package org.csstudio.dal.ui.dnd.rfc;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

public class WeakStructuredSelection implements
		IStructuredSelection {
	private WeakReference<List> weakListReference;

	public WeakStructuredSelection(IStructuredSelection originalSelection) {
		List originalList = originalSelection.toList();
		weakListReference = new WeakReference<List>(originalList);
	}

	@Override
	public boolean isEmpty() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.isEmpty();
		} else {
			return true;
		}
	}

	@Override
	public Object getFirstElement() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.iterator().next();
		} else {
			return null;
		}
	}

	@Override
	public Iterator iterator() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.iterator();
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object[] toArray() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.toArray();
		} else {
			return null;
		}
	}

	@Override
	public List toList() {
		List originalList = weakListReference.get();
		if (originalList != null) {
			return originalList;
		} else {
			return null;
		}
	}

}