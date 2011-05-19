/**
 * 
 */
package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author shroffk
 * 
 */
public class GlazedSortPropertyComparator implements
		IDirectionalComparator<Channel> {

	private String propertyName;
	private int col;
	private int direction;

	/**
	 * @param propertyName
	 * @param col
	 * @param direction
	 */
	public GlazedSortPropertyComparator(String propertyName, int col,
			int direction) {
		this.propertyName = propertyName;
		this.col = col;
		this.direction = direction;
	}

	@Override
	public void setDirection(int dir) {
		this.direction = dir;
	}

	@Override
	public int compare(Channel channel1, Channel channel2) {
		int ret = compareProperties(channel1, channel2);
		if (direction == SWT.DOWN)
			ret = -ret;
		return ret;

	}

	private int compareProperties(Channel ch1, Channel ch2) {
		Property prop1 = getProperty(ch1, propertyName);
		Property prop2 = getProperty(ch2, propertyName);
		if ((prop1 == null) && (prop2 == null))
			return 0;
		else if (prop1 == null)
			return -1;
		else if (prop2 == null)
			return +1;
		else
			return smartCompare(prop1.getValue(), prop2.getValue());
	}

	private int smartCompare(String value1, String value2) {
		Pattern p2 = Pattern.compile("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
		if (p2.matcher(value1).matches() && p2.matcher(value2).matches()) {
			return Double.valueOf(value1).compareTo(Double.valueOf(value2));
		} else if (p2.matcher(value1).matches()
				&& !p2.matcher(value2).matches()) {
			return +1;
		} else if (!p2.matcher(value1).matches()
				&& p2.matcher(value2).matches()) {
			return -1;
		} else if (!p2.matcher(value1).matches()
				&& !p2.matcher(value2).matches()) {
			return Collator.getInstance().compare(value1, value2);
		}
		return 0;
	}

	private Property getProperty(Channel channel, String propertyName) {
		Collection<Property> property = Collections2.filter(
				channel.getProperties(),
				new PropertyNamePredicate(propertyName));
		if (property.size() == 1)
			return property.iterator().next();
		else
			return null;
	}

	private class PropertyNamePredicate implements Predicate<Property> {

		private String propertyName;

		PropertyNamePredicate(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public boolean apply(Property input) {
			if (input.getName().equals(propertyName))
				return true;
			return false;
		}
	}

}
