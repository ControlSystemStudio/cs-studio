/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser;

import org.csstudio.autocomplete.IAutoCompleteProvider;

/**
 * Common content types, can be extended to define specific content types.
 * Used by {@link IContentParser} & {@link IAutoCompleteProvider} to filter
 * content.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ContentType {

	public static ContentType Empty = new ContentType("Empty");
	public static ContentType Undefined = new ContentType("Undefined");
	public static ContentType FormulaFunction = new ContentType("FormulaFunction");
	public static ContentType PV = new ContentType("PV");
	public static ContentType PVName = new ContentType("PVName");
	public static ContentType PVField = new ContentType("PVField");
	public static ContentType PVParam = new ContentType("PVParam");
	public static ContentType PVDataSource = new ContentType("PVDataSource");

	private final String value;

	protected ContentType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

}
