/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import org.w3c.dom.CharacterData;

/**
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class CDataWrapper extends AbstractNodeWrapper {

	private final CharacterData cdata;
	private final String originalData;

	public CDataWrapper(CharacterData cdata) {
		this.cdata = cdata;
		this.originalData = cdata.getData();
	}

	@Override
	protected String getOriginalData() {
		return originalData;
	}

	@Override
	protected String getData() {
		return cdata.getData();
	}

	@Override
	protected void setData(String data) {
		cdata.setData(data);
	}

	@Override
	protected void reset() {
		cdata.setData(originalData);
	}

}
