/**
 * 
 */
package org.csstudio.channelfinder.util;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author shroffk
 *
 */
public class ChannelUtilities {
	
	public static Property getProperty(Channel channel, String propertyName) {
		Collection<Property> property = Collections2.filter(
				channel.getProperties(),
				new PropertyNamePredicate(propertyName));
		if (property.size() == 1)
			return property.iterator().next();
		else
			return null;
	}

	private static class PropertyNamePredicate implements Predicate<Property> {

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
