package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Tag;

import java.util.Comparator;

public class TagComparator implements Comparator<Tag> {

	@Override
	public int compare(Tag o1, Tag o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
