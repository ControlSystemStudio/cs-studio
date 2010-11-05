package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Property;

import java.util.Comparator;

public class PropertyComparator implements Comparator<Property> {

	@Override
	public int compare(Property o1, Property o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
