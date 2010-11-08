/**
 * 
 */
package org.csstudio.channelfinder.views;

import java.util.Comparator;

/**
 * @author shroffk
 *
 */
public class ChannelComparator implements Comparator<ChannelItem> {

	@Override
	public int compare(ChannelItem o1, ChannelItem o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
