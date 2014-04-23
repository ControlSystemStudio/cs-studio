/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.autocomplete.IAutoCompleteProvider;

/**
 * Descriptor used in {@link IContentParser} and {@link IAutoCompleteProvider}
 * to describe a content matching a PV.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class PVDescriptor extends ContentDescriptor {

	private String name;
	private String field;
	private Map<String, String> params;

	public PVDescriptor() {
		params = new HashMap<String, String>();
	}

	public void addParam(String name, String value) {
		params.put(name, value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "PVDescriptor [name=" + name + ", field=" + field + ", params="
				+ params + ", toString()=" + super.toString() + "]";
	}

}
