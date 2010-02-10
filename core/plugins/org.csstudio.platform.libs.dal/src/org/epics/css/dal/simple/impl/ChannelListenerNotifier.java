package org.epics.css.dal.simple.impl;

import java.util.Iterator;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;

import com.cosylab.util.ListenerList;

public class ChannelListenerNotifier {
	
	private ListenerList listeners = new ListenerList(ChannelListener.class);
	private AnyDataChannel channel;
	private LinkListener<DynamicValueProperty<?>> linkListener = new LinkListener<DynamicValueProperty<?>>() {
		public void connected(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void connectionFailed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void connectionLost(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void destroyed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void disconnected(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void resumed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void suspended(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}
	};
	private DynamicValueListener dvListener = new DynamicValueAdapter() {
		public void valueChanged(DynamicValueEvent event) {
			fireChannelDataUpdate();
		}
	};
	
	public ChannelListenerNotifier(AnyDataChannel channel) {
		this.channel = channel;
	}
	
	public synchronized void addChannelListener(ChannelListener listener) {
		listeners.add(listener);
		if (listeners.size() == 1) subscribe(channel);
	}
	
	public synchronized void removeChannelListener(ChannelListener listener) {
		listeners.remove(listener);
		if (listeners.size() == 0) unsubscribe();
	}
	
	public void subscribe(AnyDataChannel channel) {
		if (this.channel != channel) unsubscribe();
		this.channel = channel;
		if (channel == null) return;
		channel.getData().getParentProperty().addLinkListener(linkListener);
		channel.getData().getParentProperty().addDynamicValueListener(dvListener);
	}
	
	public void unsubscribe() {
		if (channel == null) return;
		channel.getData().getParentProperty().removeDynamicValueListener(dvListener);
		channel.getData().getParentProperty().removeLinkListener(linkListener);
	}
	
	private void fireChannelStateUpdate() {
		for (ChannelListener listener : getChannelListenerArray()) {
			listener.channelStateUpdate(channel);
		}
	}
	
	private void fireChannelDataUpdate() {
		for (ChannelListener listener : getChannelListenerArray()) {
			listener.channelDataUpdate(channel);
		}
	}
	
	private ChannelListener[] getChannelListenerArray() {
		ChannelListener[] array;
		synchronized (this) {
			array = new ChannelListener[listeners.size()];
			int i = 0;
			for (Iterator<Object> iterator = listeners.iterator(); iterator.hasNext();) {
				Object type = (Object) iterator.next();
				array[i++] = (ChannelListener) type;
			}
		}
		return array;
	}

}
