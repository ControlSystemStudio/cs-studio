package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import org.csstudio.utility.caSnooperUi.parser.ChannelStructure;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Class for sorting columns in a table
 * 
 * @author rkosir
 *
 */
public class SnooperSorter extends ViewerSorter{

	private int sortIndex;
	private int order = -1;
	
	public SnooperSorter(int index, boolean sort) {
		super();
		if(sort)
			order = -1;
		else
			order = 1;
		sortIndex = index;
	}

	public int compare(Viewer viewer, Object o1, Object o2){
		ChannelStructure chan1 = (ChannelStructure) o1;
		ChannelStructure chan2 = (ChannelStructure) o2;
//		System.out.println(sortIndex);
		switch (sortIndex){
		case 0:
		case 3:			
			return order * (chan1.getId()-chan2.getId());
		case 1:
			return order * (chan1.getClientAddress().compareToIgnoreCase(chan2.getClientAddress()));
		case 2:
			return order * (chan1.getAliasName().compareToIgnoreCase(chan2.getAliasName()));
		}
		return 1;
	}
}
