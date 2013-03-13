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

	@Override
	public PVListResult listPVs(final String name, final int limit) {
		PVListResult pvList = new PVListResult();
		Pattern p = PVNameHelper.convertToPattern(name);

		int added = 0;
		pvList.setCount(Activator.getDefault().getHistory().size());
		for (String entry : Activator.getDefault().getHistory()) {
			if (p.matcher(entry).matches()) {
				pvList.add(entry);
				added++;
			}
			if (added == limit)
				return pvList;
		}
		return pvList;
	}

	@Override
	public void cancel() { }

}
