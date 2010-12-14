package org.csstudio.multichannelviewer;

import java.util.Collection;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ChannelGroupContentProvider implements IStructuredContentProvider {

	private TableViewer viewer;
	private Collection<ICSSChannel> channels;
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		channels = (Collection<ICSSChannel>) newInput;			
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return channels.toArray();
	}
}
