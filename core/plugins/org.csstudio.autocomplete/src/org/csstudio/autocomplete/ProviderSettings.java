/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import org.csstudio.autocomplete.preferences.Preferences;

/**
 * A provider as defined via OSGI services and preferences.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ProviderSettings implements Comparable<ProviderSettings> {

	/**
	 * Provider name as defined in the OSGI component.
	 */
	private final String name;
	/**
	 * Implementation of {@link IAutoCompleteProvider} defined in the OSGI
	 * component.
	 */
	private final IAutoCompleteProvider provider;
	/**
	 * High level provider are always displayed regardless of defined
	 * preferences. This boolean is optional (default=false) in the OSGI
	 * component declaration.
	 */
	private final boolean highLevelProvider;
	/**
	 * Maximum displayed results for this provided, defined by preferences.
	 */
	private Integer maxResults;
	/**
	 * Used to manage display order, high level provider always appears first
	 * and others follow the order defined in preferences.
	 */
	private Integer index = -1;

	public ProviderSettings(String name, IAutoCompleteProvider provider,
			boolean highLevelProvider) {
		this.name = name;
		this.provider = provider;
		this.highLevelProvider = highLevelProvider;
		this.maxResults = Preferences.getDefaultMaxResults();
	}

	public ProviderSettings(ProviderSettings ps) {
		this.name = ps.name;
		this.provider = ps.provider;
		this.highLevelProvider = ps.highLevelProvider;
		this.maxResults = ps.maxResults;
		this.index = ps.index;
	}

	public ProviderSettings(ProviderSettings ps, Integer index) {
		this(ps);
		this.index = index;
	}

	public ProviderSettings(ProviderSettings ps, Integer index,
			Integer maxResults) {
		this(ps);
		this.maxResults = maxResults;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public IAutoCompleteProvider getProvider() {
		return provider;
	}

	public boolean isHighLevelProvider() {
		return highLevelProvider;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ProviderSettings other = (ProviderSettings) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(ProviderSettings ps) {
		if (ps == null)
			return -1;
		if (this.isHighLevelProvider() && ps.isHighLevelProvider())
			return this.name.compareTo(ps.getName());
		if (this.isHighLevelProvider() && !ps.isHighLevelProvider())
			return -1;
		if (!this.isHighLevelProvider() && ps.isHighLevelProvider())
			return 1;
		if (this.index == ps.getIndex())
			return this.name.compareTo(ps.getName());
		return this.index.compareTo(ps.getIndex());
	}

	@Override
	public String toString() {
		return "ProviderSettings [name=" + name + ", provider=" + provider
				+ ", highLevelProvider=" + highLevelProvider + ", maxResults="
				+ maxResults + ", index=" + index + "]";
	}

}
