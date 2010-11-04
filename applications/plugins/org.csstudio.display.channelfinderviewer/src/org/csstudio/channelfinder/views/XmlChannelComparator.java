/**
 * 
 */
package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.model.XmlChannel;

import java.util.Comparator;

/**
 * @author shroffk
 *
 */
public class XmlChannelComparator implements Comparator<XmlChannel> {

	@Override
	public int compare(XmlChannel o1, XmlChannel o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
