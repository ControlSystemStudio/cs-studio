package org.epics.css.dal.simple.impl;

import java.util.Iterator;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueCondition;
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
	
	private DynamicValueCondition lastCondition = null;
	private boolean initialStateUpdate = false;
	private boolean initialDataUpdate = false;
	
	private LinkListener<DynamicValueProperty<?>> linkListener = new LinkListener<DynamicValueProperty<?>>() {
		public void connected(ConnectionEvent<DynamicValueProperty<?>> e) {
			fireChannelStateUpdate();
		}

		public void operational(ConnectionEvent<DynamicValueProperty<?>> e) {
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
		
		public void conditionChange(DynamicValueEvent event) {
			DynamicValueCondition cond = event.getCondition();
			if (initialStateUpdate && cond != null && lastCondition != null && lastCondition.areStatesEqual(cond) && lastCondition.hasValue() == cond.hasValue()) {
				return;
			}
			lastCondition = cond;
			initialStateUpdate = true;
			fireChannelStateUpdate();
		}

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
		listeners.add(listener);
		
		if (initialStateUpdate) listener.channelStateUpdate(channel);
		if (initialDataUpdate) listener.channelDataUpdate(channel);
		
		if (listeners.size() == 1) subscribe(channel);
	}
	
	public synchronized void removeChannelListener(ChannelListener listener) {
		listeners.remove(listener);
		if (listeners.size() == 0) unsubscribe();
	}
	
	public synchronized ChannelListener[] getChannelListeners() {
		return (ChannelListener[]) listeners.toArray(new ChannelListener[listeners.size()]);
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
