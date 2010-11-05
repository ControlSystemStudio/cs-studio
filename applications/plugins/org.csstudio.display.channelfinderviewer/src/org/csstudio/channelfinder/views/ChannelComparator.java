/**
 * 
 */
package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;

import java.util.Comparator;

/**
 * @author shroffk
 *
 */
public class ChannelComparator implements Comparator<Channel> {

	@Override
	public int compare(Channel o1, Channel o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
