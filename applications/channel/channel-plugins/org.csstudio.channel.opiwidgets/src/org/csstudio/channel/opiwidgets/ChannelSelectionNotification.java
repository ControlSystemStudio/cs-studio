package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.LocalUtilityPvManagerBridge;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

public abstract class ChannelSelectionNotification {
	
	private final ChannelNotificationExpression notificationExpression;
	private final ISelectionProvider selectionProvider;
	private final LocalUtilityPvManagerBridge notification;
	
	private final ISelectionChangedListener listener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() instanceof IStructuredSelection) {
				Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (element != null) {
					String notificationString = notificationFor(element);
					if (notificationString != null) {
						notification.write(notificationString);
					} else {
						notification.write("");
					}
				}
			} else {
				notification.write("");
			}
		}
	};
	
	protected abstract String notificationFor(Object selection);
	
	public ChannelNotificationExpression getNotificationExpression() {
		return notificationExpression;
	}
	
	public <T extends Widget & ISelectionProvider>  ChannelSelectionNotification(String notificationPv,
			String notificationExpression, T widget) {
		this(notificationPv, notificationExpression, widget, widget);
	}
	
	
	public ChannelSelectionNotification(String notificationPv,
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
