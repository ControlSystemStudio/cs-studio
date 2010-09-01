package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.model.XmlTag;

import java.util.Comparator;

public class XmlTagComparator implements Comparator<XmlTag> {

	@Override
	public int compare(XmlTag o1, XmlTag o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareTo(o2.getName());
	}

}
