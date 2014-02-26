/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;


public class Breaktable {

	private final String name;
	private final Float[][] values;

	public Breaktable(String name, int i, int j) {
		this.name = name;
		values = new Float[i][j];
	}

	public void addValue(int i, int j, Float value) {
		if (i < values.length)
			if (j < values[i].length)
				values[i][j] = value;
	}

	public String getName() {
		return name;
	}

	public Float[][] getValues() {
		return values;
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
		Breaktable other = (Breaktable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Breaktable [name=" + name + "]";
	}

}
