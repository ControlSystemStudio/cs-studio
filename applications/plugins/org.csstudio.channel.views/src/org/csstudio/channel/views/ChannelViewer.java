package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class ChannelViewer extends ViewPart {
	private ChannelQueryInputBar inputBar;
	private ChannelViewerWidget channelsViewWidget;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.ChannelViewer";
	
	public ChannelViewer() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblQuery = new Label(parent, SWT.NONE);
		FormData fd_lblQuery = new FormData();
		fd_lblQuery.top = new FormAttachment(0, 8);
		fd_lblQuery.left = new FormAttachment(0, 5);
		lblQuery.setLayoutData(fd_lblQuery);
		lblQuery.setText("Query:");

		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, Activator
				.getDefault().getDialogSettings(), "channelviewer.query");
		FormData fd_inputBar = new FormData();
		fd_inputBar.top = new FormAttachment(0, 5);
		fd_inputBar.left = new FormAttachment(lblQuery, 6);
		inputBar.setLayoutData(fd_inputBar);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setChannelQuery((ChannelQuery) event.getNewValue());
				}
			}
		});
				
		channelsViewWidget = new ChannelViewerWidget(parent, SWT.None);
		fd_inputBar.right = new FormAttachment(channelsViewWidget, 0, SWT.RIGHT);
		fd_lblQuery.left = new FormAttachment(channelsViewWidget, 0, SWT.LEFT);
		FormData fd_channelsViewWidget = new FormData();
		fd_channelsViewWidget.bottom = new FormAttachment(100, -5);
		fd_channelsViewWidget.top = new FormAttachment(inputBar, 6);
		fd_channelsViewWidget.left = new FormAttachment(0, 5);
		fd_channelsViewWidget.right = new FormAttachment(100, -5);
		channelsViewWidget.setLayoutData(fd_channelsViewWidget);
		
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);
		PopupMenuUtil.installPopupForView(channelsViewWidget, getSite(), channelsViewWidget);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void setChannelQuery(ChannelQuery query) {
		inputBar.setChannelQuery(query);
		channelsViewWidget.setChannelQuery(query);
	}


}
