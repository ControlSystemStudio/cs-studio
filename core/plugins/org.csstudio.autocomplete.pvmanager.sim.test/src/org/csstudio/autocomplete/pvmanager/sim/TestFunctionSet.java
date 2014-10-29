/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TestFunctionSet extends DSFunctionSet {

	public static String name = SimDSFunctionSet.name;

	/**
	 * Creates a new set.
	 */
	public TestFunctionSet() {
		super(initSet());
	}

	private static DSFunctionSetDescription initSet() {
		DSFunctionSetDescription setDescription = new DSFunctionSetDescription(
				name, "Simulation DataSource Test");
		DSFunction function = null;
		DSFunction polymorphicFunction = null;

		// const
		polymorphicFunction = new DSFunction("const", null, true, false);
		polymorphicFunction.addArgument("number", Double.class);
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("const", null, true, false);
		function.addArgument("string", String.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("const", null, true, true);
		function.addArgument("args", Double.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("const", null, true, true);
		function.addArgument("args", String.class);
		polymorphicFunction.addPolymorphicFunction(function);

		// constbis
		function = new DSFunction("constbis", null, true, false);
		function.addArgument("number", Double.class);
		setDescription.addFunction(function);

		// foo
		function = new DSFunction("foo", null, true, false);
		setDescription.addFunction(function);

		return setDescription;
	}

}
