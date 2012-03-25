package org.csstudio.sds.ui.internal.viewer;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * A selection implementation that references selected objects only weak, using
 * {@link WeakReference} instances.
 * 
 * @author swende
 * 
 */
public class WeakStructuredSelection implements IStructuredSelection {
	private WeakReference<List<Object>> weakListReference;

	public WeakStructuredSelection(IStructuredSelection originalSelection) {
		@SuppressWarnings("unchecked")
		List<Object> originalList = originalSelection.toList();
		weakListReference = new WeakReference<List<Object>>(originalList);
	}

	@Override
	public boolean isEmpty() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.isEmpty();
		} else {
			return true;
		}
	}

	@Override
	public Object getFirstElement() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.iterator().next();
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator iterator() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.iterator();
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object[] toArray() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList.toArray();
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List toList() {
		List<Object> originalList = weakListReference.get();
		if (originalList != null) {
			return originalList;
		} else {
			return null;
		}
	}

}