/**
 * 
 */
package org.csstudio.channelfinder.views;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * @author shroffk
 * 
 */
public class TagCellLabelProvider extends CellLabelProvider {

	private String tagName;

	public TagCellLabelProvider(String tagName) {
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
