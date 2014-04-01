/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;

import java.util.Map;
import java.util.TreeMap;

public class Field implements Comparable<Field> {

	private final String name;
	private final String type;
	private Map<String, Object> rules;

	public Field(String name, String type) {
		this.name = name;
		this.type = type;
		this.rules = new TreeMap<String, Object>();
	}

	public void addRule(String name, Object value) {
		this.rules.put(name, value);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getRules() {
		return rules;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
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
		Field other = (Field) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Field f) {
		return this.name.compareTo(f.getName());
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + ", rules=" + rules
				+ "]";
	}

}