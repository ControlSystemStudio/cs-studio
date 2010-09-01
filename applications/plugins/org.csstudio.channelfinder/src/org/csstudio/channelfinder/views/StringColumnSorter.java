/**
 * 
 */
package org.csstudio.channelfinder.views;

import java.text.Collator;

import gov.bnl.channelfinder.model.XmlChannel;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author shroffk
 * 
 */
public class StringColumnSorter extends AbstractColumnViewerSorter {

	public StringColumnSorter(ColumnViewer viewer, TableViewerColumn column) {
		super(viewer, column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.channelfinder.views.AbstractColumnViewerSorter#doCompare
	 * (org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected int doCompare(Viewer viewer, Object e1, Object e2) {
		return Collator.getInstance().compare(((XmlChannel) e1).getName(),
				((XmlChannel) e2).getName());
	}

}
