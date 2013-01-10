package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.viewers.CellLabelProvider;

public interface OlogTableColumnDescriptor {
		
	public String getText();
	
	public String getTooltip();
	
	public int getWeight();
	
	public CellLabelProvider getCellLabelProvider();	
	

}
