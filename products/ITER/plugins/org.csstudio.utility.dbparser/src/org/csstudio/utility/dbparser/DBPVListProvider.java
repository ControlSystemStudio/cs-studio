/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.regex.Pattern;

import org.csstudio.pvnames.IPVListProvider;
import org.csstudio.pvnames.PVListResult;
import org.csstudio.pvnames.PVNameHelper;

public class DBPVListProvider implements IPVListProvider {

	@Override
	public PVListResult listPVs(final String pattern, final int limit) {
		PVListResult result = new PVListResult();
		Pattern p = PVNameHelper.convertToPattern(pattern);
		if (p == null)
			return result;
		
		result.setCount(DBContextValueHolder.get().countPV(p));
		for (String pv : DBContextValueHolder.get().findPV(p, limit))
			result.add(pv);
		return result;
	}

	@Override
	public void cancel() { }

}
