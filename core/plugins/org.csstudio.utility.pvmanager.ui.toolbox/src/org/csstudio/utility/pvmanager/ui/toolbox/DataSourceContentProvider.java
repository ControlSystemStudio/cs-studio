package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PVManager;

public class DataSourceContentProvider implements IStructuredContentProvider {
	
	public static Object ALL = new Object() {
		public String toString() {return "All";};
	};

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
		List<DataSourceChannel> channels = new ArrayList<DataSourceChannel>();
		if (inputElement == ALL) {
			for (Map.Entry<String, DataSource> entry : ((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().entrySet()) {
				addChannels(channels, entry.getKey(), entry.getValue());
			}
		} else {
			String dataSourceName = (String) inputElement;
			DataSource dataSource = ((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().get(dataSourceName);
			addChannels(channels, dataSourceName, dataSource);
		}
		
		Collections.sort(channels);
		return channels.toArray();
	}
	
	private void addChannels(List<DataSourceChannel> channels, String dataSourceName, DataSource dataSource) {
		for (ChannelHandler channelHandler : dataSource.getChannels().values()) {
			channels.add(new DataSourceChannel(dataSourceName, channelHandler));
		}
	}

}
