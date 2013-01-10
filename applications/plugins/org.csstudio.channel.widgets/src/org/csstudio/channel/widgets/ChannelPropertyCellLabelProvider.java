/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * @author shroffk
 * 
 */
public class ChannelPropertyCellLabelProvider extends CellLabelProvider {

	private String propertyName;

	public ChannelPropertyCellLabelProvider(String propertyName) {
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
		Property property = ((Channel) cell.getElement()).getProperty(propertyName);
		if(property == null)
			cell.setText("");
		else
			cell.setText(property.getValue());
	}

}
