/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;

public class DBPVListProvider implements IAutoCompleteProvider {

	public static final String NAME = "DB Files";

	@Override
	public AutoCompleteResult listResult(final String type, final String name,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		Pattern p = AutoCompleteHelper.convertToPattern(name);
		if (p == null)
			return result;

		result.setCount(DBContextValueHolder.get().countPV(p));
		for (String pv : DBContextValueHolder.get().findPV(p, limit))
			result.add(pv);
		return result;
	}

	@Override
	public void cancel() {
	}

}
