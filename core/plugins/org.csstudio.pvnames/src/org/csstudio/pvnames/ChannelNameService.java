/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Get PV List from providers (see {@link IPVListProvider} + extension point)
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class ChannelNameService {

	private static ChannelNameService instance;
	private Map<String, IPVListProvider> providers;

	private ChannelNameService() {
		try {
			providers = getProviders();
		} catch (CoreException e) {
			providers = new HashMap<String, IPVListProvider>();
		}
	}

	public static ChannelNameService getInstance() {
		if (null == instance) {
			instance = new ChannelNameService();
		}
		return instance;
	}

	public List<PVListResult> get(final String name, final int limit) {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService get: " + name + " <<");
		List<PVListResult> pvList = new ArrayList<PVListResult>();
		if (name == null || name.isEmpty())
			return pvList; // Empty list

		// Execute them in parallel
		final ExecutorService executors = Executors
				.newFixedThreadPool(providers.size());
		final List<Future<PVListResult>> results = new ArrayList<Future<PVListResult>>();
		for (final Entry<String, IPVListProvider> entry : providers.entrySet()) {
			final IPVListProvider current_provider = entry.getValue();
			final Callable<PVListResult> callable = new Callable<PVListResult>() {
				@Override
				public PVListResult call() throws Exception {
					PVListResult result = current_provider.listPVs(name + "*", limit);
					result.setProvider(entry.getKey());
					return result;
				}
			};
			results.add(executors.submit(callable));
		}
		for (Future<PVListResult> result : results) {
			try {
				final PVListResult info = result.get();
				if (info != null)
					pvList.add(info);
			} catch (Exception ex) {
				if (!(ex instanceof InterruptedException)) {
					Activator.getLogger().log(Level.WARNING,
							"PVListProvider error", ex);
				}
			}
		}
		executors.shutdown();
		return pvList;
	}

	public void cancel() {
		Activator.getLogger().log(Level.FINE,
				">> ChannelNameService canceled <<");
		for (IPVListProvider provider : providers.values())
			provider.cancel();
	}

	public boolean hasProviders() {
		return !providers.isEmpty();
	}

	/**
	 * Read PV lists providers extension points from plugin.xml.
	 * 
	 * @return Map<String, IPVListProvider>, extension points referenced by
	 *         their scheme.
	 * @throws CoreException
	 *             if implementations don't provide the correct IPVListProvider
	 */
	private Map<String, IPVListProvider> getProviders() throws CoreException {
		final Map<String, IPVListProvider> map = new HashMap<String, IPVListProvider>();
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(IPVListProvider.EXTENSION_POINT);
		for (IConfigurationElement element : extensions) {
			final String scheme = element.getAttribute("name");
			final IPVListProvider provider = (IPVListProvider) element
					.createExecutableExtension("class");
			map.put(scheme, provider);
		}
		return map;
	}

}
