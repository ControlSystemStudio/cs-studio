/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

/**
 * Used by {@link AutoCompleteService} to notify that a
 * {@link IAutoCompleteProvider} has returned a {@link AutoCompleteResult}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface IAutoCompleteResultListener {

	public void handleResult(Long uniqueId, Integer index,
			AutoCompleteResult result);

}
