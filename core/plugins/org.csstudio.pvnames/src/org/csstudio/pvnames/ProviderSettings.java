/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

public class ProviderSettings {

	private final String name;
	private final IAutoCompleteProvider provider;
	private final int max_results;

	public ProviderSettings(String name, IAutoCompleteProvider provider,
			int max_results) {
		this.name = name;
		this.provider = provider;
		this.max_results = max_results;
	}

	public String getName() {
		return name;
	}

	public IAutoCompleteProvider getProvider() {
		return provider;
	}

	public int getMax_results() {
		return max_results;
	}

}
