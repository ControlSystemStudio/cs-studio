package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PVManager;

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
		String dataSourceName = (String) inputElement;
		DataSource dataSource = ((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().get(dataSourceName);
		List<DataSourceChannel> channels = new ArrayList<DataSourceChannel>();
		for (ChannelHandler<?> channelHandler : dataSource.getChannels().values()) {
			channels.add(new DataSourceChannel(dataSourceName, channelHandler));
		}
		Collections.sort(channels);
		return channels.toArray();
	}

}
