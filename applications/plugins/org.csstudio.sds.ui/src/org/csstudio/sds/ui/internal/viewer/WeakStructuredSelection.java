package org.csstudio.sds.ui.internal.viewer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
	private List<WeakReference> weakList;

	public WeakStructuredSelection(IStructuredSelection originalSelection) {
		List originalList = originalSelection.toList();
		if (originalList != null) {
			weakList = new ArrayList<WeakReference>();

			for (Object o : originalList) {
				weakList.add(new WeakReference<Object>(o));
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return getAlive().isEmpty();
	}

	@Override
	public Object getFirstElement() {
		List<Object> alive = getAlive();
		return alive.isEmpty() ? null : alive.get(0);
	}

	@Override
	public Iterator iterator() {
		return getAlive().iterator();
	}

	@Override
	public int size() {
		return getAlive().size();
	}

	@Override
	public Object[] toArray() {
		return getAlive().toArray();
	}

	@Override
	public List toList() {
		return getAlive();
	}

	private List<Object> getAlive() {
		List<Object> result = new ArrayList<Object>();

		if (weakList != null) {
			for (WeakReference<Object> wr : weakList) {
				Object o = wr.get();
				if (o != null) {
					result.add(o);
				}
			}
		}

		return result;
	}
}