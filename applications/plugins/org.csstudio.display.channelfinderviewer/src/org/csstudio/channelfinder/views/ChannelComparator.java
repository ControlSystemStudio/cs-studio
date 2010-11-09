/**
 * 
 */
package org.csstudio.channelfinder.views;

import java.util.Comparator;

import org.csstudio.utility.channel.ICSSChannel;

/**
 * @author shroffk
 *
 */
public class ChannelComparator implements Comparator<ICSSChannel> {

	@Override
	public int compare(ICSSChannel o1, ICSSChannel o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
