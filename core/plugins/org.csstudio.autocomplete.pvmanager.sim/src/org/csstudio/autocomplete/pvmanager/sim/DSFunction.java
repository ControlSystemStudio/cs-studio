/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition for a function that can be integrated in the data source language.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DSFunction implements Comparable<DSFunction> {

	private final String name;
	private String description;
	private String tooltip;
	private final Class<?> returnType;
	/**
	 * Whether the function is a pure function, given the same arguments always
	 * returns the same result.
	 */
	private final boolean isPure;
	/**
	 * Whether the function takes a variable number of arguments.
	 * <p>
	 * Variable arguments can only be at the end of the argument list, and have
	 * the same type.
	 */
	private final boolean isVarArgs;
	/**
	 * The ordered list of the arguments name.
	 */
	private List<String> argumentNames;
	/**
	 * The ordered list of the arguments type.
	 */
	private List<Class<?>> argumentTypes;

	private List<DSFunction> polymorphicFunctions;

	public DSFunction(String name, Class<?> returnType, boolean isPure,
			boolean isVarArgs) {
		super();
		this.name = name;
		this.returnType = returnType;
		this.isPure = isPure;
		this.isVarArgs = isVarArgs;
		argumentNames = new ArrayList<String>();
		argumentTypes = new ArrayList<Class<?>>();
		polymorphicFunctions = new ArrayList<>();
	}

	public void addPolymorphicFunction(DSFunction function) {
		polymorphicFunctions.add(function);
	}

	public boolean isPolymorphic() {
		return polymorphicFunctions.size() > 0;
	}

	public void addArgument(String name, Class<?> type) {
		argumentNames.add(name);
		argumentTypes.add(type);
	}

	public int getNbArgs() {
		return argumentNames.size();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public boolean isPure() {
		return isPure;
	}

	public boolean isVarArgs() {
		return isVarArgs;
	}

	public List<String> getArgumentNames() {
		return argumentNames;
	}

	public List<Class<?>> getArgumentTypes() {
		return argumentTypes;
	}

	public List<DSFunction> getPolymorphicFunctions() {
		return polymorphicFunctions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DSFunction other = (DSFunction) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(DSFunction arg0) {
		return name.compareTo(arg0.getName());
	}

}
