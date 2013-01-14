package org.csstudio.display.multichannelviewer.views;

import java.util.Comparator;

import gov.bnl.channelfinder.api.Channel;

public class ChannelPropertyComparator implements Comparator<Channel> {

	private final AlphanumComparator alphaNum = new AlphanumComparator();
	private final String sortProperty;

	/**
	 * Create a property sorter for the property <tt>sortProperty</tt>
	 * 
	 * @param sortProperty
	 */
	public ChannelPropertyComparator(String sortProperty) {
		System.out.println("create sort property "+ sortProperty);
		this.sortProperty = sortProperty;
	}

	@Override
	public int compare(Channel ch1, Channel ch2) {
//		System.out.println(ch1.getProperty(sortProperty).getValue() + " " + ch2.getProperty(sortProperty).getValue());
		return alphaNum.compare(ch1.getProperty(sortProperty).getValue(),
				ch2.getProperty(sortProperty).getValue());
	}

}
