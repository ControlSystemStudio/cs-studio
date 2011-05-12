/**
 * 
 */
package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;

import org.csstudio.channelfinder.util.ChannelUtilities;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * @author shroffk
 * 
 */
public class PropertyCellLabelProvider extends CellLabelProvider {

	private String propertyName;

	public PropertyCellLabelProvider(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.
	 * viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		cell.setText(ChannelUtilities.getProperty((Channel) cell.getElement(),
				this.propertyName).getValue());
	}

}
