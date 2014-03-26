/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

import java.util.Collection;
import java.util.HashSet;

/**
 * The description for a function set to be used in data source language.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DSFunctionSetDescription {

	String name;
	String description;
	Collection<DSFunction> functions = new HashSet<>();

	/**
	 * A new function set description.
	 * 
	 * @param name the name of the function set
	 * @param description the description of the function set
	 */
	public DSFunctionSetDescription(String name, String description) {
		this.name = name;
		this.description = description;
		if (!DSFunctionSet.namePattern.matcher(name).matches()) {
			throw new IllegalArgumentException(
					"Name must start by a letter and only consist of letters and numbers");
		}
	}

	/**
	 * Adds a function in the set.
	 * 
	 * @param function the function to add
	 * @return this description
	 */
	public DSFunctionSetDescription addFunction(DSFunction function) {
		functions.add(function);
		return this;
	}
}
