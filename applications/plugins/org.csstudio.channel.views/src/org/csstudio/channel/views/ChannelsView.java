package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.ChannelsViewWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class ChannelsView extends ViewPart {
	private ChannelQueryInputBar inputBar;
	private ChannelsViewWidget channelsViewWidget;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.ChannelsView";
	
	public ChannelsView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gl_parent = new GridLayout(2, false);
		parent.setLayout(gl_parent);
		
		Label lblQuery = new Label(parent, SWT.NONE);
		lblQuery.setText("Query: ");

		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, Activator
				.getDefault().getDialogSettings(), "waterfall.query");
		inputBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setChannelQuery((ChannelQuery) event.getNewValue());
				}
			}
		});
				
		channelsViewWidget = new ChannelsViewWidget(parent, SWT.None);
		channelsViewWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
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
