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
	
	private final ChannelNotificationString notificationExpression;
	private final ChannelTreeByPropertyWidget widget;
	private final LocalUtilityPvManagerBridge notification;
	
	private final ISelectionChangedListener listener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
				ChannelTreeByPropertyNode node = (ChannelTreeByPropertyNode)
						((IStructuredSelection) event.getSelection()).getFirstElement();
				if (node.isSubQuery()) {
					notification.write(notificationExpression.notification(node.getPropertiesAndValues()));
				} else {
					notification.write(notificationExpression.notification(node.getNodeChannels().get(0)));
				}
			} else {
				notification.write("");
			}
		}
	};
	
	public ChannelTreeByPropertySelectionNotification(String notificationPv,
			String notificationExpression, ChannelTreeByPropertyWidget widget) {
		this.notificationExpression = new ChannelNotificationString(notificationExpression);
		this.widget = widget;
		
		notification = new LocalUtilityPvManagerBridge(notificationPv);
		
		widget.addSelectionChangedListener(listener);
		widget.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		});
	}
	
	public void close() {
		widget.removeSelectionChangedListener(listener);
		notification.close();
	}

}
