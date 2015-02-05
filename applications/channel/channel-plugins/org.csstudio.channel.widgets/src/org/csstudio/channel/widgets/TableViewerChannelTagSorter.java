/**
 * 
 */
package org.csstudio.channel.widgets;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;

import com.swtdesigner.TableViewerColumnSorter;
/**
 * @author shroffk
 * 
 */
public class TableViewerChannelTagSorter extends TableViewerColumnSorter {

	private String tagName;

	public TableViewerChannelTagSorter(TableViewerColumn column,
			String tagName) {
		super(column);
		this.tagName = tagName;
	}

	@Override
	protected int doCompare(Viewer viewer, Object e1, Object e2) {
		return compareTags((Channel) e1, (Channel) e2);
	}

	private int compareTags(Channel ch1, Channel ch2) {
		boolean containsTag1 = ch1.getTags().contains(tag(tagName).build());
		boolean containsTag2 = ch2.getTags().contains(tag(tagName).build());
		if (containsTag1 == containsTag2)
			return 0;
		else if (containsTag1)
			return -1;
		else if (containsTag2)
			return +1;
		return 0;
	}
}
