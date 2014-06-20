package org.csstudio.askap.logviewer.ui;

import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.LogObject.LogComparatorField;
import org.csstudio.askap.utility.icemanager.LogObject.LogObjectComparator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class LogObjectViewerComparator extends ViewerComparator {
	
	private Direction sortDirection = Direction.ASCENDING;
	LogObjectComparator comparator = new LogObjectComparator(LogComparatorField.timeStamp);
	
	public static enum Direction {
		DESCENDING,
		ASCENDING
	}

	public LogObjectViewerComparator() {
		super();
	}

	public void setComparatorField(LogComparatorField compField) {
		if (comparator.getComparatorField().equals(compField)) {
			if (sortDirection.equals(Direction.DESCENDING))
				sortDirection = Direction.ASCENDING;
			else
				sortDirection = Direction.DESCENDING;
		} else {
			comparator.setComparatorField(compField);
			sortDirection = Direction.ASCENDING;
		}		
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int result = comparator.compare((LogObject)e1, (LogObject)e2);
		
		if (sortDirection == Direction.DESCENDING)
			result = -result;
		
		return result;
	}
}
