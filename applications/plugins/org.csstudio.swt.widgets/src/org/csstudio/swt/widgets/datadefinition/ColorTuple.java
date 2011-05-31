/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.datadefinition;

import org.eclipse.swt.graphics.RGB;

/**A color Tuple which include a double value and rgb value.
 * @author Xihui Chen
 *
 */
public class ColorTuple implements Comparable<ColorTuple>{
	public double value;
	public RGB rgb;
	public ColorTuple(double value, RGB rgb) {
		this.value = value;
		this.rgb = rgb;
	}
	public int compareTo(ColorTuple o) {
		if(value < o.value)
			return -1;
		else if(this.equals(o))
			return 0;
		else			
			return 1;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rgb == null) ? 0 : rgb.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ColorTuple other = (ColorTuple) obj;
		if (rgb == null) {
			if (other.rgb != null)
				return false;
		} else if (!rgb.equals(other.rgb))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}		
	
	
	
	
}
