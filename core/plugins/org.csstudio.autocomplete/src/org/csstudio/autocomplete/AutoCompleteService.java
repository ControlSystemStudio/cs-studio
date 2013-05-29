/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Get PV List from providers (see {@link IAutoCompleteProvider} + extension point)
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class AutoCompleteService {

	private class ProviderTask implements Runnable {
		
		private final Long uniqueId;
		private final Integer index;

		private final String type;
		private final String name;
		
		private final ProviderSettings settings;
		private final IAutoCompleteResultListener listener;
		private boolean canceled = false;

		public ProviderTask(final Long uniqueId, final Integer index,
				final String type, final String name,
				final ProviderSettings settings,
				final IAutoCompleteResultListener listener) {
			this.index = index;
			this.uniqueId = uniqueId;
			this.type = type;
			this.name = name;
			this.settings = settings;
			this.listener = listener;
		}

		@Override
		public void run() {
			AutoCompleteResult result = settings.getProvider().listResult(type,
					name + "*", settings.getMax_results());
			result.setProvider(settings.getName());
			if (result != null && !canceled)
				listener.handleResult(uniqueId, index, result);
			synchronized (workQueue) {
				workQueue.remove(this);
				// System.out.println("REMOVED: " + task);
			}
		}

		public void cancel() {
			canceled = true;
			settings.getProvider().cancel();
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
					+ ", type=" + type + ", name=" + name + ", canceled="
					+ canceled + "]";
		}
		
	}
	
	private static AutoCompleteService instance;
	private Map<String, IAutoCompleteProvider> providers;
	private Map<String, List<ProviderSettings>> providerSettings;
	private ProviderSettings defaultProvider;
	private List<ProviderTask> workQueue;

	private AutoCompleteService() {
		try {
			providers = getOSGIServices();
			if (providers.get("History") != null) {
				defaultProvider = new ProviderSettings("History",
						providers.get("History"),
						Preferences.getDefaultMaxResults());
			}
		} catch (Exception e) {
			providers = new HashMap<String, IAutoCompleteProvider>();
		}
		providerSettings = new HashMap<String, List<ProviderSettings>>();
		workQueue = new ArrayList<ProviderTask>();
	}

	public static AutoCompleteService getInstance() {
		if (null == instance) {
			instance = new AutoCompleteService();
		}
		return instance;
	}

	public void get(final Long uniqueId, final String type, final String name,
			final IAutoCompleteResultListener listener) {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService get: " + name + " for type: " + type + " <<");

		if (name == null || name.isEmpty())
			return;

		List<ProviderSettings> providerList = findProviders(type);
		if (providerList == null || providerList.isEmpty())
			return;

		// Execute them in parallel
		int index = 0; // Usefull to keep the order
		for (final ProviderSettings settings : providerList) {
			final ProviderTask task = new ProviderTask(uniqueId, index, type,
					name, settings, listener);
			synchronized (workQueue) {
				workQueue.add(task);
				// System.out.println("ADDED: " + task);
			}
			new Thread(task).start();
			index++;
		}
	}
	
	public void cancel(final String type) {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService canceled for type: " + type + " <<");
		synchronized (workQueue) {
			for (ProviderTask task : workQueue)
				task.cancel();
		}
	}

	public boolean hasProviders(final String type) {
		return !providerSettings.get(type).isEmpty();
	}

	/**
	 * Read PV lists providers extension points from plugin.xml.
	 * 
	 * @return Map<String, IPVListProvider>, extension points referenced by their scheme.
	 * @throws CoreException if implementations don't provide the correct IPVListProvider
	 */
	@SuppressWarnings("unused")
	private Map<String, IAutoCompleteProvider> getProviders() throws CoreException {
		final Map<String, IAutoCompleteProvider> map = new HashMap<String, IAutoCompleteProvider>();
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(IAutoCompleteProvider.EXTENSION_POINT);
		for (IConfigurationElement element : extensions) {
			final String scheme = element.getAttribute("name");
			final IAutoCompleteProvider provider = (IAutoCompleteProvider) element
					.createExecutableExtension("class");
			map.put(scheme, provider);
		}
		return map;
	}
	
	private Map<String, IAutoCompleteProvider> getOSGIServices()
			throws InvalidSyntaxException {
		final Map<String, IAutoCompleteProvider> map = new HashMap<String, IAutoCompleteProvider>();

		BundleContext context = Activator.getBundleContext();
		Collection<ServiceReference<IAutoCompleteProvider>> references = context
				.getServiceReferences(IAutoCompleteProvider.class, null);

		for (ServiceReference<IAutoCompleteProvider> ref : references) {
			String name = (String) ref.getProperty("component.name");
			IAutoCompleteProvider provider = (IAutoCompleteProvider) context.getService(ref);
			map.put(name, provider);
		}
		return map;
	}

	private List<ProviderSettings> parseProviderList(String pref) {
		List<ProviderSettings> providerList = new LinkedList<ProviderSettings>();
		if (pref == null || pref.isEmpty())
			return null;

		// Parse provider list
		StringTokenizer st_provider = new StringTokenizer(pref, ";");
		while (st_provider.hasMoreTokens()) {
			String token_provider = st_provider.nextToken();

			if (token_provider.contains(",")) {
				String name = token_provider.substring(0,
						token_provider.indexOf(',')).trim();
				int max_results = Integer.parseInt(token_provider.substring(
						token_provider.indexOf(',') + 1,
						token_provider.length()).trim());
				if (providers.get(name) != null)
					providerList.add(new ProviderSettings(name, providers
							.get(name), max_results));
			} else {
				String name = token_provider.trim();
				if (providers.get(name) != null)
					providerList.add(new ProviderSettings(name, providers
							.get(name), Preferences.getDefaultMaxResults()));
			}
		}
		return providerList;
	}

	private List<ProviderSettings> findProviders(String type) {
		List<ProviderSettings> providerList = providerSettings.get(type);
		if (providerList == null || providerList.isEmpty()) {
			providerList = parseProviderList(Preferences.getProviders(type));
			if (providerList == null || providerList.isEmpty()) {
				if (defaultProvider != null) {
					providerList = new LinkedList<ProviderSettings>();
					providerList.add(defaultProvider);
					providerSettings.put(type, providerList);
				}
			} else {
				providerSettings.put(type, providerList);
			}
		}
		return providerList;
	}

}
