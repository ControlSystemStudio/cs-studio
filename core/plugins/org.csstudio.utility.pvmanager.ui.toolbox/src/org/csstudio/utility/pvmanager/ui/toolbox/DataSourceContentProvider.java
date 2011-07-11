package org.csstudio.utility.pvmanager.ui.toolbox;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.DataSource;

public class DataSourceContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// Nothing to do
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Nothing to do
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] channels =  ((DataSource) inputElement).getChannels().values().toArray();
		return channels;
	}

}
