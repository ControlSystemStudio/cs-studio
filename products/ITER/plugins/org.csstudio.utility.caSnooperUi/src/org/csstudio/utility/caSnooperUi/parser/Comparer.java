package org.csstudio.utility.caSnooperUi.parser;

import java.util.Comparator;

public class Comparer implements Comparator<ChannelStructure>{

	public int compare(ChannelStructure o1, ChannelStructure o2) {
		if(o1.getRepeats() == o2.getRepeats())
			return o2.getAliasName().compareTo(o1.getAliasName());
		if(o1.getRepeats() < o2.getRepeats())
			return 1;
		else if (o1.getRepeats() > o2.getRepeats())
			return -1;
		return 0;
	}
	
}
