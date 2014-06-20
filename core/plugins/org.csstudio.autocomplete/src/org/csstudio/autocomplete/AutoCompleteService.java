/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;

import org.csstudio.autocomplete.impl.DataSourceProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.IContentParser;
import org.csstudio.autocomplete.preferences.Preferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Service which handles content parsing (see {@link IContentParser}) and
 * requesting proposals from defined providers (see
 * {@link IAutoCompleteProvider}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteService {

	/**
	 * Flag that controls the printing of debug info.
	 */
	public static final boolean DEBUG = false;

	private class ProviderTask implements Runnable {

		private final Long uniqueId;
		private final Integer index;
		private final ContentDescriptor desc;
		private final ProviderSettings settings;
		private final IAutoCompleteResultListener listener;
		private boolean canceled = false;

		public ProviderTask(final Long uniqueId, final Integer index,
				final ContentDescriptor desc, final ProviderSettings settings,
				final IAutoCompleteResultListener listener) {
			this.index = index;
			this.uniqueId = uniqueId;
			this.desc = desc;
			this.settings = settings;
			this.listener = listener;
		}

		@Override
		public void run() {
			AutoCompleteResult result = settings.getProvider().listResult(desc, settings.getMaxResults());
			if (result != null
					&& !settings.getName().equals(DataSourceProvider.NAME))
				// TODO: find a better solution to hide DataSourceProvider...
				result.setProvider(settings.getName());
			if (!canceled)
				listener.handleResult(uniqueId, index, result);
			synchronized (workQueue) {
				workQueue.remove(this);
			}
		}

		public void cancel() {
			settings.getProvider().cancel();
			canceled = true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((index == null) ? 0 : index.hashCode());
			result = prime * result
					+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProviderTask other = (ProviderTask) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (index == null) {
				if (other.index != null)
					return false;
			} else if (!index.equals(other.index))
				return false;
			if (uniqueId == null) {
				if (other.uniqueId != null)
					return false;
			} else if (!uniqueId.equals(other.uniqueId))
				return false;
			return true;
		}

		private AutoCompleteService getOuterType() {
			return AutoCompleteService.this;
		}

		@Override
		public String toString() {
			return "ProviderTask [uniqueId=" + uniqueId + ", index=" + index
					+ ", desc=" + desc + ", settings=" + settings
					+ ", canceled=" + canceled + "]";
		}
	}

	private class ScheduledContent implements Comparable<ScheduledContent> {
		public ProviderSettings settings;
		public ContentDescriptor desc;

		@Override
		public String toString() {
			return "ScheduledContent [settings=" + settings + ", desc=" + desc + "]";
		}

		@Override
		public int compareTo(ScheduledContent sc) {
			return this.settings.compareTo(sc.settings);
		}
	}

	private static AutoCompleteService instance;
	private Map<String, ProviderSettings> providerByName;
	private Map<String, List<ProviderSettings>> providersByType;
	private ProviderSettings defaultProvider;
	private List<ProviderTask> workQueue;
	private List<IContentParser> parsers;

	private AutoCompleteService() {
		try {
			providerByName = getOSGIProviders();
			if (providerByName.get("History") != null)
				defaultProvider = providerByName.get("History");
			parsers = getOSGIParsers();
		} catch (Exception e) {
			if (providerByName == null)
				providerByName = new TreeMap<String, ProviderSettings>();
			if (parsers == null)
				parsers = new ArrayList<IContentParser>();
		}
		providersByType = new TreeMap<String, List<ProviderSettings>>();
		workQueue = new ArrayList<ProviderTask>();
	}

	public static AutoCompleteService getInstance() {
		if (null == instance) {
			instance = new AutoCompleteService();
		}
		return instance;
	}

	public int get(final Long uniqueId, final AutoCompleteType acType,
			final String content, final IAutoCompleteResultListener listener) {
		AutoCompletePlugin.getLogger().log(Level.FINE,
				">> ChannelNameService get: " + content + " for type: " + acType.value() + " <<");

		if (content == null || content.isEmpty())
			return 0; // no result

		// Useful to handle default data source
		ContentDescriptor desc = new ContentDescriptor();
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "org.csstudio.utility.pv");
		// Note: "default_type" is a common setting between pv, pv.ui, pvmanager and pvmanager.ui
		// They need to be kept synchronized.
		if (store != null)
			desc.setDefaultDataSource(store.getString("default_type") + "://");
		desc.setContentType(ContentType.Undefined);
		desc.setAutoCompleteType(acType);
		desc.setOriginalContent(content);
		desc.setValue(content);

		List<ContentDescriptor> descList = parseContent(desc);
		if (DEBUG) {
			System.out.println("=============================================");
			System.out.println("--- ContentDescriptor list ---");
			for (ContentDescriptor ct : descList)
				System.out.println(ct);
		}

		int index = 0; // Useful to keep the order
		List<ScheduledContent> providerList = retrieveProviders(acType,
				descList);
		if (DEBUG) {
			System.out.println("--- Associated Content ---");
		}
		// Execute them in parallel
		for (ScheduledContent sc : providerList) {
			if (DEBUG) {
				System.out.println(sc.settings + " => " + sc.desc);
			}
			final ProviderTask task = new ProviderTask(uniqueId, index,
					sc.desc, sc.settings, listener);
			synchronized (workQueue) {
				workQueue.add(task);
			}
			new Thread(task).start();
			index++;
		}
		return index;
	}

	public void cancel(final String type) {
		AutoCompletePlugin.getLogger().log(Level.FINE,
				">> ChannelNameService canceled for type: " + type + " <<");
		synchronized (workQueue) {
			for (ProviderTask task : workQueue)
				task.cancel();
		}
	}

	public boolean hasProviders(final String type) {
		return !providersByType.get(type).isEmpty();
	}

	/* Get providers from OSGI services */
	private Map<String, ProviderSettings> getOSGIProviders()
			throws InvalidSyntaxException {
		final Map<String, ProviderSettings> map = new TreeMap<String, ProviderSettings>();

		BundleContext context = AutoCompletePlugin.getBundleContext();
		Collection<ServiceReference<IAutoCompleteProvider>> references = context
				.getServiceReferences(IAutoCompleteProvider.class, null);

		for (ServiceReference<IAutoCompleteProvider> ref : references) {
			IAutoCompleteProvider provider = (IAutoCompleteProvider) context.getService(ref);
			String name = (String) ref.getProperty("component.name");
			boolean highLevelProvider = false;
			String prop = (String) ref.getProperty("highLevelProvider");
			if (prop != null && !prop.isEmpty())
				highLevelProvider = Boolean.valueOf(prop);
			map.put(name, new ProviderSettings(name, provider, highLevelProvider));
		}
		return map;
	}

	/* Get providers from OSGI services */
	private List<IContentParser> getOSGIParsers() throws InvalidSyntaxException {
		final List<IContentParser> list = new ArrayList<IContentParser>();

		BundleContext context = AutoCompletePlugin.getBundleContext();
		Collection<ServiceReference<IContentParser>> references = context
				.getServiceReferences(IContentParser.class, null);

		for (ServiceReference<IContentParser> ref : references) {
			IContentParser parser = (IContentParser) context.getService(ref);
			list.add(parser);
		}
		return list;
	}

	/* Read the list of providers from preference string */
	private List<ProviderSettings> parseProviderList(String pref) {
		List<ProviderSettings> providerList = new ArrayList<ProviderSettings>();

		if (pref != null && !pref.isEmpty()) {
			int index = -1;
			StringTokenizer st_provider = new StringTokenizer(pref, ";");
			while (st_provider.hasMoreTokens()) {
				String token_provider = st_provider.nextToken();

				if (token_provider.contains(",")) {
					String name = token_provider.substring(0, token_provider.indexOf(',')).trim();
					int max_results = Integer.parseInt(token_provider.substring(token_provider.indexOf(',') + 1, token_provider.length()).trim());
					if (providerByName.get(name) != null)
						providerList.add(new ProviderSettings(providerByName.get(name), ++index, max_results));
				} else {
					String name = token_provider.trim();
					if (providerByName.get(name) != null)
						providerList.add(new ProviderSettings(providerByName.get(name), ++index));
				}
			}
		}

		// add default provider
		if (providerList.isEmpty() && defaultProvider != null)
			providerList.add(defaultProvider);

		// add high level providers
		// TODO: all type have high level providers defined
		// => need restrictions ?
		for (ProviderSettings ps : providerByName.values())
			if (ps.isHighLevelProvider() && !providerList.contains(ps))
				providerList.add(new ProviderSettings(ps));

		Collections.sort(providerList);
		return providerList;
	}

	/* Associate 1 provider per descriptor */
	private List<ScheduledContent> retrieveProviders(AutoCompleteType acType,
			List<ContentDescriptor> tokens) {
		// retrieve the list from preferences
		String type = acType.value();
		if (providersByType.get(type) == null)
			providersByType.put(type, parseProviderList(Preferences.getProviders(type)));
		List<ProviderSettings> definedProviderList = new ArrayList<ProviderSettings>(providersByType.get(type));

		// associate descriptor to a provider
		List<ScheduledContent> acceptedProviderList = new ArrayList<ScheduledContent>();
		for (ContentDescriptor desc : tokens) {
			Iterator<ProviderSettings> it = definedProviderList.iterator();
			while (it.hasNext()) {
				ProviderSettings settings = it.next();
				if (settings.getProvider().accept(desc.getContentType())) {
					ScheduledContent sc = new ScheduledContent();
					sc.desc = desc;
					sc.settings = settings;
					acceptedProviderList.add(sc);
					it.remove();
				}
			}
		}
		Collections.sort(acceptedProviderList);
		return acceptedProviderList;
	}

	/* Handle recursive parsing of a content desc. */
	private List<ContentDescriptor> parseContent(ContentDescriptor desc) {
		List<ContentDescriptor> tokenList = new ArrayList<ContentDescriptor>();

		ContentDescriptor newDesc = null;
		// backup data
		int startIndex = desc.getStartIndex();
		int endIndex = desc.getEndIndex();
		AutoCompleteType acType = desc.getAutoCompleteType();
		String defaultDatasource = desc.getDefaultDataSource();
		String originalContent = desc.getOriginalContent();
		// cancel replay
		desc.setReplay(false);

		for (IContentParser parser : parsers) {
			if (parser.accept(desc) && (newDesc = parser.parse(desc)) != null) {
				newDesc.setAutoCompleteType(acType);
				newDesc.setDefaultDataSource(defaultDatasource);
				newDesc.setOriginalContent(originalContent);
				// update indexes
				newDesc.setStartIndex(newDesc.getStartIndex() + startIndex);
				newDesc.setEndIndex(newDesc.getEndIndex() + endIndex);
				if (newDesc.isReplay()) { // recursive
					tokenList.addAll(parseContent(newDesc));
				} else {
					tokenList.add(newDesc);
				}
			}
		}
		if (tokenList.isEmpty()) {
			desc.setContentType(ContentType.Undefined);
			tokenList.add(desc);
		}
		return tokenList;
	}

}
