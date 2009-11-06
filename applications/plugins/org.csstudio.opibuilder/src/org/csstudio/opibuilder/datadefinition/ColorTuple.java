package org.csstudio.opibuilder.datadefinition;

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
		else if(value == o.value)
			return 0;
		else			
			return 1;
	}		
}