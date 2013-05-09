/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.LinkedHashSet;
import java.util.Set;

public class AutoCompleteResult {

	private Set<String> results;
	private int count;
	private String provider;

	public AutoCompleteResult() {
		this.results = new LinkedHashSet<String>();
		this.count = 0;
	}

	public void merge(AutoCompleteResult other, int limit) {
		this.results.addAll(other.getResults());
		Set<String> tmpSet = new LinkedHashSet<String>();
		for (String pv : results)
			if (tmpSet.size() <= limit)
				tmpSet.add(pv);
		this.results = tmpSet;
		this.count = this.count + other.getCount();
	}

	public void add(String name) {
		results.add(name);
	}

	public Set<String> getResults() {
		return results;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
