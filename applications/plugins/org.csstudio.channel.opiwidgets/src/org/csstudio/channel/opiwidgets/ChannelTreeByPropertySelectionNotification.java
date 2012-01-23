package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyNode;
import org.csstudio.channel.widgets.LocalUtilityPvManagerBridge;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

public class ChannelTreeByPropertySelectionNotification {
	
	private final ChannelNotificationExpression notificationExpression;
	private final ISelectionProvider selectionProvider;
	private final LocalUtilityPvManagerBridge notification;
	
	private final ISelectionChangedListener listener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
				String notificationString = notificationFor(((IStructuredSelection) event.getSelection()).getFirstElement());
				if (notificationString != null) {
					notification.write(notificationString);
				} else {
					notification.write(notificationString);
				}
			} else {
				notification.write("");
			}
		}
	};
	
	protected String notificationFor(Object selection) {
		ChannelTreeByPropertyNode node = (ChannelTreeByPropertyNode) selection;
		if (node.isSubQuery()) {
			return notificationExpression.notification(node.getPropertiesAndValues());
		} else {
			return notificationExpression.notification(node.getNodeChannels().get(0));
		}
	}
	
	public <T extends Widget & ISelectionProvider>  ChannelTreeByPropertySelectionNotification(String notificationPv,
			String notificationExpression, T widget) {
		this(notificationPv, notificationExpression, widget, widget);
	}
	
	
	public ChannelTreeByPropertySelectionNotification(String notificationPv,
			String notificationExpression, ISelectionProvider selectionProvider, Widget widget) {
		this.notificationExpression = new ChannelNotificationExpression(notificationExpression);
		this.selectionProvider = selectionProvider;
		
		notification = new LocalUtilityPvManagerBridge(notificationPv);
		
		selectionProvider.addSelectionChangedListener(listener);
		widget.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		});
	}
	
	public void close() {
		selectionProvider.removeSelectionChangedListener(listener);
		notification.close();
	}

}
