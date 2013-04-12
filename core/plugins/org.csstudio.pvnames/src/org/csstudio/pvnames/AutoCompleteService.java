/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

	private static AutoCompleteService instance;
	private Map<String, IAutoCompleteProvider> providers;
	private Map<String, List<ProviderSettings>> providerSettings;
	private ProviderSettings defaultProvider;

	private AutoCompleteService() {
		try {
			providers = getOSGIServices();
			providerSettings = initProviders(Preferences.getProviders());
			if (providers.get("History") != null) {
				defaultProvider = new ProviderSettings("History",
						providers.get("History"),
						Preferences.getDefaultMaxResults());
			}
		} catch (Exception e) {
			providers = new HashMap<String, IAutoCompleteProvider>();
			providerSettings = new HashMap<String, List<ProviderSettings>>();
		}
	}

	public static AutoCompleteService getInstance() {
		if (null == instance) {
			instance = new AutoCompleteService();
		}
		return instance;
	}

	public List<AutoCompleteResult> get(final String type, final String name) {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService get: " + name + " for type: " + type + " <<");
		
		List<AutoCompleteResult> acList = new ArrayList<AutoCompleteResult>();
		if (name == null || name.isEmpty())
			return acList; // Empty list
		
		List<ProviderSettings> providerList = providerSettings.get(type);
		if (providerList == null || providerList.isEmpty()) {
			if (defaultProvider != null) {
				providerList = new LinkedList<ProviderSettings>();
				providerList.add(defaultProvider);
			} else {
				return acList; // Empty list
			}
		}

		// Execute them in parallel
		final ExecutorService executors = Executors.newFixedThreadPool(providerList.size());
		final List<Future<AutoCompleteResult>> resultList = new ArrayList<Future<AutoCompleteResult>>();
		for (final ProviderSettings settings : providerList) {
			final Callable<AutoCompleteResult> callable = new Callable<AutoCompleteResult>() {
				@Override
				public AutoCompleteResult call() throws Exception {
					AutoCompleteResult result = settings.getProvider()
							.listResult(type, name + "*", settings.getMax_results());
					result.setProvider(settings.getName());
					return result;
				}
			};
			resultList.add(executors.submit(callable));
		}
		for (Future<AutoCompleteResult> result : resultList) {
			try {
				final AutoCompleteResult info = result.get();
				if (info != null)
					acList.add(info);
			} catch (Exception ex) {
				if (!(ex instanceof InterruptedException)) {
					Activator.getLogger().log(Level.WARNING,
							"AutoCompleteProvider error", ex);
				}
			}
		}
		executors.shutdown();
		return acList;
	}

	public void cancel(final String type) {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService canceled for type: " + type + " <<");
		List<ProviderSettings> providerList = providerSettings.get(type);
		if (providerList == null)
			return;
		for (ProviderSettings settings : providerList)
			settings.getProvider().cancel();
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

	private Map<String, List<ProviderSettings>> initProviders(String pref) {
		Map<String, List<ProviderSettings>> providerMap = new HashMap<String, List<ProviderSettings>>();
		if (pref == null || pref.isEmpty())
			return providerMap;

		// Parse types
		StringTokenizer st_type = new StringTokenizer(pref, "|");
		while (st_type.hasMoreTokens()) {
			List<ProviderSettings> providerList = new LinkedList<ProviderSettings>();
			String token_type = st_type.nextToken();
			
			String type = token_type.substring(0, token_type.indexOf(':')).trim();
			String list = token_type.substring(token_type.indexOf(':') + 1,
					token_type.length()).trim();
			
			// Parse provider list
			StringTokenizer st_provider = new StringTokenizer(list, ";");
			while (st_provider.hasMoreTokens()) {
				String token_provider = st_provider.nextToken();
				
				if (token_provider.contains(",")) {
					String name = token_provider.substring(0,
							token_provider.indexOf(',')).trim();
					int max_results = Integer.parseInt(token_provider
							.substring(token_provider.indexOf(',') + 1,
									token_provider.length()).trim());
					if (providers.get(name) != null)
						providerList.add(new ProviderSettings(name, providers
								.get(name), max_results));
				} else {
					String name = token_provider.trim();
					if (providers.get(name) != null)
						providerList
								.add(new ProviderSettings(name, providers.get(name), 
										Preferences.getDefaultMaxResults()));
				}
			}
			providerMap.put(type, providerList);
		}
		return providerMap;
	}

}
