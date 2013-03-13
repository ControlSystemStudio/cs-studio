/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.Set;
import java.util.TreeSet;

public class PVListResult {

	private Set<String> pvs;
	private int count;

	public PVListResult() {
		this.pvs = new TreeSet<String>();
		this.count = 0;
	}

	public void merge(PVListResult other, int limit) {
		this.pvs.addAll(other.getPvs());
		Set<String> tmpSet = new TreeSet<String>();
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

}
