/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Menu {

	private final String name;
	private Map<String, String> choices;
	private List<Include> includes;

	public Menu(String name) {
		this.name = name;
		this.choices = new TreeMap<String, String>();
		this.includes = new ArrayList<Include>();
	}

	public void addChoice(String name, String value) {
		this.choices.put(name, value);
	}

	public void addInclude(Include include) {
		this.includes.add(include);
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getChoices() {
		return choices;
	}

	public void setChoices(Map<String, String> choices) {
		this.choices = choices;
	}

	public List<Include> getIncludes() {
		return includes;
	}

	public void setIncludes(List<Include> includes) {
		this.includes = includes;
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
		Menu other = (Menu) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Menu [name=" + name + ", choices=" + choices + ", includes="
				+ includes + "]";
	}

}
