/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;

public class AutoCompleteHistoryProvider implements IAutoCompleteProvider {

	public static final String NAME = "History";

	@Override
	public AutoCompleteResult listResult(final String type, final String name,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		Pattern p = AutoCompleteHelper.convertToPattern(name);
		if (p == null)
			return result;

		int added = 0;
		int count = 0;
		LinkedList<String> fifo = Activator.getDefault().getHistory(type);
		if (fifo == null)
			return result; // Empty result
		for (String entry : fifo) {
			if (p.matcher(entry).matches()) {
				if (added < limit) {
					result.add(entry);
					added++;
				}
				count++;
			}
		}
		result.setCount(count);
		return result;
	}

	@Override
	public void cancel() {
	}

}
