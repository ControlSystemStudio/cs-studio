package org.eclipse.nebula.jface.gridviewer.internal;

import java.util.List;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * FIXME
 */
public class SelectionWithFocusRow extends StructuredSelection {
	private Object focusElement;

	/**
	 * FIXME
	 * @param elements
	 * @param focusElement
	 * @param comparer
	 */
	public SelectionWithFocusRow(List elements, Object focusElement, IElementComparer comparer) {
        super(elements,comparer);
        this.focusElement = focusElement;
	}
	
	/**
	 * FIXME
	 * @return the focus element
	 */
	public Object getFocusElement() {
		return focusElement;
	}

}
