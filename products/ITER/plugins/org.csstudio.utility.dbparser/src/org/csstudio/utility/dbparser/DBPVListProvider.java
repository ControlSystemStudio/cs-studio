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

public class DBPVListProvider implements IPVListProvider {

	@Override
	public PVListResult listPVs(Pattern pattern, int limit) {
		PVListResult result = new PVListResult();
		result.setCount(DBContextValueHolder.get().countPV(pattern));
		for (String pv : DBContextValueHolder.get().findPV(pattern, limit))
			result.add(pv);
		return result;
	}

}
