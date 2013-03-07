/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
	private static Map<String, IPVListProvider> providers;
	
	private static String SINGLE_REPLACE_CHAR = "?";
	private static String MULTI_REPLACE_CHAR = "*";

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

	public PVListResult get(String name, int limit) {
		PVListResult result = new PVListResult();
		if (name == null || name.isEmpty())
			return result;
		Pattern p = createPattern(name);
		for (IPVListProvider provider : providers.values())
			result.merge(provider.listPVs(p, limit), limit);
		return result;
	}
	
	private Pattern createPattern(String name) {
		String regex = name.replace(MULTI_REPLACE_CHAR, ".+");
		regex = regex.replace(SINGLE_REPLACE_CHAR, ".");
		regex += ".*";
		return Pattern.compile(regex);
	}

	/**
	 * Read PV lists providers extension points from plugin.xml.
	 * 
	 * @return Map<String, IPVListProvider>, extension points referenced by their scheme.
	 * @throws CoreException if implementations don't provide the correct IPVListProvider
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
