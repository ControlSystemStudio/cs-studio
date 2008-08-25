package org.csstudio.config.authorizeid;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides data for second table (eaig, eair).
 * @author Rok Povsic
 */
public class AuthorizeIdLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnText(Object element, int columnIndex) {
		
		if(element instanceof AuthorizeIdEntry) {
			AuthorizeIdEntry entry = (AuthorizeIdEntry) element;
			switch(columnIndex) {
			case AuthorizeIdView.COL_EAIG:
				return entry.getEaig();
			case AuthorizeIdView.COL_EAIR:
				return entry.getEair();
			default:
				return null;
			}
		}
		return null;
	}


}
