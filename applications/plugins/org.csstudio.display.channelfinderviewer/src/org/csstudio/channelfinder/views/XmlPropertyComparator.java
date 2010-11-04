package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.model.XmlProperty;

import java.util.Comparator;

public class XmlPropertyComparator implements Comparator<XmlProperty> {

	@Override
	public int compare(XmlProperty o1, XmlProperty o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
