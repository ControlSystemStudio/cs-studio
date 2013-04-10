/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames.ui;

import java.util.regex.Pattern;

import org.csstudio.pvnames.IPVListProvider;
import org.csstudio.pvnames.PVListResult;
import org.csstudio.pvnames.PVNameHelper;

public class PVContentHistoryProvider implements IPVListProvider {

	public static final String NAME = "History";

	@Override
	public PVListResult listPVs(final String name, final int limit) {
		PVListResult pvList = new PVListResult();
		Pattern p = PVNameHelper.convertToPattern(name);
		if (p == null)
			return pvList;

		int added = 0;
		int count = 0;
		for (String entry : Activator.getDefault().getHistory()) {
			if (p.matcher(entry).matches()) {
				if (added < limit) {
					pvList.add(entry);
					added++;
				}
				count++;
			}
		}
		pvList.setCount(count);
		return pvList;
	}

	@Override
	public void cancel() {
	}

}
