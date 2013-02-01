package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import java.util.ArrayList;

import org.csstudio.utility.caSnooperUi.parser.ChannelStructure;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for the changelog table viewer.
 * 
 * @author rkosir
 */
class TableContentProvider implements IStructuredContentProvider {


	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		TableLabelProvider.resetCount();
		ArrayList<ChannelStructure> tmp = (ArrayList<ChannelStructure>) inputElement;
		Object[] tmpO = new Object[tmp.size()];
		for(int i=0;i<tmp.size();i++)
			tmpO[i] = tmp.get(i);
		return tmpO;
	}
}
