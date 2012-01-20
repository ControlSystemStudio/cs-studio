package org.csstudio.dal.simple.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.context.ConnectionEvent;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;

public class ChannelListenerNotifier {
	
	private final ConcurrentHashMap<Integer, ChannelListener> listeners = new ConcurrentHashMap<Integer, ChannelListener>();
	private AnyDataChannel channel;
	
	private DynamicValueCondition lastCondition = null;
	private boolean initialStateUpdate = false;
	private boolean initialDataUpdate = false;
	
	private final LinkListener<DynamicValueProperty<?>> linkListener = new LinkListener<DynamicValueProperty<?>>() {
		@Override
        public void connected(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void operational(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void connectionFailed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void connectionLost(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void destroyed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void disconnected(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void resumed(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		@Override
        public void suspended(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}
	};
	private final DynamicValueListener dvListener = new DynamicValueAdapter() {
		
		@Override
        public void conditionChange(DynamicValueEvent event) {
			DynamicValueCondition cond = event.getCondition();
			if (initialStateUpdate && cond != null && lastCondition != null && lastCondition.areStatesEqual(cond) && lastCondition.hasValue() == cond.hasValue()) {
				return;
			}
			lastCondition = cond;
			initialStateUpdate = true;
			fireChannelStateUpdate();
		}

		@Override
        public void valueChanged(DynamicValueEvent event) {
			initialDataUpdate = true;
			fireChannelDataUpdate();
		}
	};
//	private PropertyChangeListener pcListener = new PropertyChangeListener() {
//		public void propertyChange(java.beans.PropertyChangeEvent evt) {
//			if (evt.getPropertyName().equals(CharacteristicInfo.C_META_DATA.getName()))
//				fireChannelDataUpdate();
//		};
//	};
	
	public ChannelListenerNotifier(AnyDataChannel channel) {
		this.channel = channel;
	}
	
	public synchronized void addChannelListener(ChannelListener listener) {
		listeners.put(listener.hashCode(), listener);
		
		if (initialStateUpdate) listener.channelStateUpdate(channel);
		if (initialDataUpdate) listener.channelDataUpdate(channel);
		
		if (listeners.size() == 1) subscribe(channel);
	}
	
	public synchronized void removeChannelListener(ChannelListener listener) {
		listeners.remove(listener.hashCode());
		if (listeners.size() == 0) unsubscribe();
	}
	
	public synchronized ChannelListener[] getChannelListeners() {
		return (ChannelListener[]) listeners.values().toArray(new ChannelListener[listeners.size()]);
	}
	
	public void subscribe(AnyDataChannel channel) {
		if (this.channel != channel) unsubscribe();
		this.channel = channel;
		if (channel == null) return;
		channel.getProperty().addLinkListener(linkListener);
		channel.getProperty().addDynamicValueListener(dvListener);
//		channel.getProperty().addPropertyChangeListener(pcListener);
	}
	
	public void unsubscribe() {
		if (channel == null) return;
//		channel.getProperty().removePropertyChangeListener(pcListener);
		channel.getProperty().removeDynamicValueListener(dvListener);
		channel.getProperty().removeLinkListener(linkListener);
		initialStateUpdate = false;
		initialDataUpdate = false;
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
		return (ChannelListener[]) listeners.values().toArray(new ChannelListener[listeners.size()]);
	}

}
