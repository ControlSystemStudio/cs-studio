package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Tag;

import java.util.Collection;
import java.util.Comparator;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.swt.SWT;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class GlazedSortTagComparator implements IDirectionalComparator<ICSSChannel> {

	private int col;
	private int direction;
	private String tagName;	

	public GlazedSortTagComparator(String tagName,int col, int direction) {
		this.tagName = tagName;
		this.col = col;
		this.direction = direction;
	}
	

	@Override
	public void setDirection(int dir) {
		this.direction = dir;
	}
	
	@Override
	public int compare(ICSSChannel channel1, ICSSChannel channel2) {
		int ret =  compareTags(channel1, channel2);
		if (direction == SWT.DOWN)
			ret = -ret;
		
		return ret;
	}

	private int compareTags(ICSSChannel channel1, ICSSChannel channel2) {
		Tag tag1 = getTag(channel1, tagName);
		Tag tag2 = getTag(channel2, tagName);
		if ((tag1 == tag2))
			return 0;
		else if (tag1 == null)
			return -1;
		else if (tag2 == null)
			return +1;
		return 0;
	}

	private Tag getTag(ICSSChannel channel, String tagName) {
		Collection<Tag> tag = Collections2.filter(channel
				.getChannel().getTags(), new TagNamePredicate(
				tagName));
		if(tag.size() == 1)
			return tag.iterator().next();
		else
			return null;
	}
	
	private class TagNamePredicate implements Predicate<Tag> {

		private String tagName;

		TagNamePredicate(String tagName) {
			this.tagName = tagName;
		}

		@Override
		public boolean apply(Tag input) {
			if (input.getName().equals(tagName))
				return true;
			return false;
		}
	}

}
