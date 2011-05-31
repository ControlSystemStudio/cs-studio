package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class ChannelGroupContentProvider implements IStructuredContentProvider {

	private TableViewer viewer;
	private Collection<Channel> channels;
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		channels = (Collection<Channel>) newInput;			
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return channels.toArray();
	}
}
