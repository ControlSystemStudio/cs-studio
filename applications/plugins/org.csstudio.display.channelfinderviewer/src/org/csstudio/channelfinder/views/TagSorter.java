/**
 * 
 */
package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Tag;

import java.util.Iterator;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author shroffk
 * 
 */
public class TagSorter extends AbstractColumnViewerSorter {

	private String tagName;

	public TagSorter(String tagName, ColumnViewer viewer,
			TableViewerColumn column) {
		super(viewer, column);
		this.tagName = tagName;
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
		return compare((Channel)e1, (Channel)e2);
	}
	
	public int compare(Channel d1, Channel d2) {
		Tag tag1 = getTag(d1, tagName);
		Tag tag2 = getTag(d2, tagName);
		if ((tag1 == tag2))
			return 0;
		else if (tag1 == null)
			return -1;
		else if (tag2 == null)
			return +1;
		return 0;
	}

	/**
	 * 
	 * @param channel
	 * @param PropertyName
	 * @return the XmlProperty with the matching name else null;
	 */
	private Tag getTag(Channel channel, String tagName) {
		Iterator<Tag> itr = channel.getTags().iterator();
		while (itr.hasNext()) {
			Tag item = itr.next();
			if (item.getName().equals(tagName)) {
				return item;
			}
		}
		return null;
	}

}
