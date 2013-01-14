package org.csstudio.logbook.ologviewer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import edu.msu.nscl.olog.api.Log;

public class OwnerCellLabelProvider extends CellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		cell.setText(((Log) cell.getElement()).getOwner());
	}

}
