/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import org.csstudio.autocomplete.parser.IContentParser;

/**
 * Common types for auto-completed fields, used by {@link IContentParser} to
 * determine if the field has to be parsed.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteType {

	public static AutoCompleteType PV = new AutoCompleteType("PV");
	public static AutoCompleteType Formula = new AutoCompleteType("Formula");

	private final String value;

	protected AutoCompleteType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static AutoCompleteType valueOf(String value) {
		switch (value) {
		case "PV": return PV;
		case "Formula": return Formula;
		default: return new AutoCompleteType(value);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AutoCompleteType other = (AutoCompleteType) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
