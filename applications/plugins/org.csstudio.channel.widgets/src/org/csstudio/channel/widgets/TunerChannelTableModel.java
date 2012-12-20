/**
 * 
 */
package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.epics.pvmanager.data.VDouble;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * @author shroffk
 * 
 */
public class TunerChannelTableModel {
	private Map<String, Channel> channels;
	private Map<String, VDouble> values;
	private Map<String, Double> weights;
	private Set<TunerChannelTableModelListener> listeners = new HashSet<TunerChannelTableModelListener>();

	public TunerChannelTableModel(List<Channel> channels) {
		super();
		this.values = Collections.emptyMap();
		if (channels == null) {
			this.channels = Collections.emptyMap();
			this.weights = Collections.emptyMap();
		} else {
			this.channels = new LinkedHashMap<String, Channel>();
			this.weights = new LinkedHashMap<String, Double>();
			for (Channel channel : channels) {
				this.channels.put(channel.getName(), channel);
				this.weights.put(channel.getName(), 1.0);
			}
		}
	}

	public void setChannels(List<Channel> channels) {
		this.values = Collections.emptyMap();
		if (channels != null) {
			this.channels = new LinkedHashMap<String, Channel>();
			this.weights = new LinkedHashMap<String, Double>();
			for (Channel channel : channels) {
				this.channels.put(channel.getName(), channel);
				this.weights.put(channel.getName(), 1.0);
			}
		} else {
			this.channels = Collections.emptyMap();
			this.weights = Collections.emptyMap();
		}
		fireDataChanged();
	}

	public List<String> getChannelNames() {
		return Collections.unmodifiableList(new ArrayList<String>(channels
				.keySet()));
	}

	public List<Channel> getChannels() {
		return Collections.unmodifiableList(new ArrayList<Channel>(channels
				.values()));
	}

	public int getRowsize() {
		return this.channels.size();
	}

	public void addPVTableModelListener(TunerChannelTableModelListener listener) {
		listeners.add(listener);
	}

	public void removePVTableModelListener(
			TunerChannelTableModelListener listener) {
		listeners.remove(listener);
	}

	private void fireDataChanged() {
		for (TunerChannelTableModelListener listener : listeners) {
			listener.dataChanged();
		}
	}

	public void updateValues(Map<String, VDouble> values) {
		this.values = values;
		fireDataChanged();
	}

	public void updateWeight(Item item, Double weight) {
		weights.put(item.getChannelName(), weight);
		fireDataChanged();
	}

	class Item {

		private String channelName;

		private Item(String channelName) {
			this.channelName = channelName;
		}

		public String getChannelName() {
			return channelName;
		}

		public Channel getChannel() {
			if (channels != null)
				return channels.get(channelName);
			else
				return null;

		}

		public VDouble getValue() {
			if (values != null && values.containsKey(channelName))
				return values.get(channelName);
			else
				return null;
		}

		public Double getWeight() {
			if (weights != null && weights.containsKey(channelName)) {
				return weights.get(channelName);
			} else {
				return null;
			}
		}

	}

	public Item[] getItems() {
		List<Item> result = new ArrayList<Item>(Collections2.transform(
				this.channels.keySet(), new Function<String, Item>() {

					@Override
					public Item apply(String input) {
						return new Item(input);
					}
				}));
		return result.toArray(new Item[channels.size()]);
	}
}
