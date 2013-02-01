package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import org.csstudio.utility.caSnooperUi.parser.ChannelStructure;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for the changelog table.
 * 
 */
class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static int count = 0;
	
	public static void resetCount(){
		count = 0;
	}
	
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	public String getColumnText(final Object element, final int columnIndex) {
		if(element instanceof ChannelStructure){
		ChannelStructure tmp = (ChannelStructure) element;
		switch(columnIndex){
			case 0:
				return ""+tmp.getId();
			case 1:
				return tmp.getClientAddress();
			case 2:
				return tmp.getAliasName();
			case 3:
				return tmp.getFrequency()+" Hz";
			}
		}
		return null;
	}
}
