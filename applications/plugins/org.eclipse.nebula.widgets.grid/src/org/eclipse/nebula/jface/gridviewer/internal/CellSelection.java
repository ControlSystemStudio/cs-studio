package org.eclipse.nebula.jface.gridviewer.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * FIXME
 */
public class CellSelection extends SelectionWithFocusRow {
	private List indicesList;
	private List elements;
	
    /**
	 * Creates a structured selection from the given <code>List</code> and
	 * element comparer. If an element comparer is provided, it will be used to
	 * determine equality between structured selection objects provided that
	 * they both are based on the same (identical) comparer. See bug 
	 * 
	 * @param elements
	 *            list of selected elements
	 * @param comparer
	 *            the comparer, or null
	 * @since 3.4
	 */
	public CellSelection(List elements, List indicesList, Object focusElement, IElementComparer comparer) {
        super(elements,focusElement,comparer);
        this.elements = new ArrayList(elements);
        this.indicesList = indicesList;
	}
	
	/**
	 * FIXME
	 * @param element
	 * @return the indices
	 */
	public List getIndices(Object element) {
		return (List) indicesList.get(elements.indexOf(element));
	}	
}
