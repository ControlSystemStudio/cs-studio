package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyNode;
import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.channel.widgets.LocalUtilityPvManagerBridge;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

public class ChannelTreeByPropertySelectionNotification {
	
	private final String notificationPv;
	private final ChannelNotificationString notificationString;
	private final ChannelTreeByPropertyWidget widget;
	private final LocalUtilityPvManagerBridge notification;
	
	private final ISelectionChangedListener listener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
				ChannelTreeByPropertyNode node = (ChannelTreeByPropertyNode)
						((IStructuredSelection) event.getSelection()).getFirstElement();
				if (node.isSubQuery()) {
					notification.write(notificationString.notification(node.getPropertiesAndValues()));
				} else {
					notification.write(notificationString.notification(node.getNodeChannels().get(0)));
				}
			} else {
				notification.write("");
			}
		}
	};
	
	public ChannelTreeByPropertySelectionNotification(String notificationPv,
			String notificationString, ChannelTreeByPropertyWidget widget) {
		this.notificationPv = notificationPv;
		this.notificationString = new ChannelNotificationString(notificationString);
		this.widget = widget;
		
		notification = new LocalUtilityPvManagerBridge(notificationPv);
		
		widget.getTreeSelectionProvider().addSelectionChangedListener(listener);
		widget.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		});
	}
	
	public void close() {
		widget.getTreeSelectionProvider().removeSelectionChangedListener(listener);
		notification.close();
	}

}
