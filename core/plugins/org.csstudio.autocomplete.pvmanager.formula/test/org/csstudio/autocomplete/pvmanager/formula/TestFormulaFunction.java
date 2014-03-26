/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.formula;

import java.util.Arrays;
import java.util.List;

import org.epics.pvmanager.formula.FormulaFunction;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TestFormulaFunction implements FormulaFunction {

	private final String name;
	private final String description;
	private final List<Class<?>> argumentTypes;
	private final List<String> argumentNames;

	public TestFormulaFunction(String name, String description, String argName) {
		this.name = name;
		this.description = description;
		this.argumentTypes = Arrays.<Class<?>> asList(Integer.class);
		this.argumentNames = Arrays.asList(argName);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isPure() {
		return true;
	}

	@Override
	public boolean isVarArgs() {
		return false;
	}

	@Override
	public List<Class<?>> getArgumentTypes() {
		return argumentTypes;
	}

	@Override
	public List<String> getArgumentNames() {
		return argumentNames;
	}

	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}

	@Override
	public Object calculate(List<Object> args) {
		return null;
	}

}
