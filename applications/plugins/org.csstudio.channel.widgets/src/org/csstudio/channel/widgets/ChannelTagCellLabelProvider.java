/**
 * 
 */
package org.csstudio.channel.widgets;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * @author shroffk
 * 
 */
public class ChannelTagCellLabelProvider extends CellLabelProvider {

	private String tagName;

	public ChannelTagCellLabelProvider(String tagName) {
		super();
		this.tagName = tagName;
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
		Channel ch = (Channel) cell.getElement();
		if(ch.getTags().contains(tag(tagName).build()))
			cell.setText("tagged");
		else
			cell.setText("");
	}

}
