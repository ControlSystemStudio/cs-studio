/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.LinkedHashSet;
import java.util.Set;

public class PVListResult {

	private Set<String> pvs;
	private int count;
	private String provider;

	public PVListResult() {
		this.pvs = new LinkedHashSet<String>();
		this.count = 0;
	}

	public void merge(PVListResult other, int limit) {
		this.pvs.addAll(other.getPvs());
		Set<String> tmpSet = new LinkedHashSet<String>();
		for (String pv : pvs)
			if (tmpSet.size() <= limit)
				tmpSet.add(pv);
		this.pvs = tmpSet;
		this.count = this.count + other.getCount();
	}

	public void add(String name) {
		pvs.add(name);
	}

	public Set<String> getPvs() {
		return pvs;
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
