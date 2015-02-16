package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelTreeByPropertyNode;
import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;

public class ChannelTreeByPropertySelectionNotification extends ChannelSelectionNotification {
	
	public ChannelTreeByPropertySelectionNotification(String notificationPv,
			String notificationExpression, ChannelTreeByPropertyWidget widget) {
		super(notificationPv, notificationExpression, widget);
	}
	
	
	protected String notificationFor(Object selection) {
		ChannelTreeByPropertyNode node = (ChannelTreeByPropertyNode) selection;
		if (node.isSubQuery()) {
			return getNotificationExpression().notification(node.getPropertiesAndValues());
		} else {
			return getNotificationExpression().notification(node.getNodeChannels().get(0));
		}
	}

}
