package org.csstudio.utility.nameSpaceSearch.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TableSorter extends ViewerSorter {
	private int spalte;
	private boolean backward;
	private int lastSort;
	private boolean lastSortBackward;

	public TableSorter(int spalte, boolean backward, int lastSort, boolean lastSortBackward) {
		this.spalte = spalte;
		this.backward = backward;
		this.lastSort = lastSort;
		this.lastSortBackward = lastSortBackward;
	}
	// Sort a table at the last two selected tableheader
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof ProcessVariable&&o2 instanceof ProcessVariable) {
			ProcessVariable vp1 = (ProcessVariable) o1;
			ProcessVariable vp2 = (ProcessVariable) o2;
			int multi =-1;
			if(backward)
				multi=1;
			int erg=multi*vp1.getPath()[spalte].compareTo(vp2.getPath()[spalte]);
			if(erg==0){
				int multi2 =-1;
				if(lastSortBackward)
					multi2=1;
				erg=multi2*vp1.getPath()[lastSort].compareTo(vp2.getPath()[lastSort]);
			}
//			for(int i=0;erg==0&&i<lastSort.length;i++){
//				erg=vp1.getPath()[lastSort[0]].compareTo(vp2.getPath()[lastSort[0]]);
//			}
			return erg;
		}else
			return 0;
	}

}
