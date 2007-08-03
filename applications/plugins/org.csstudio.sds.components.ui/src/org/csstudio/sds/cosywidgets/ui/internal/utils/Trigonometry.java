package org.csstudio.sds.cosywidgets.ui.internal.utils;

/**
 * A utility class for drawing.
 * This one contains various trigonometric functions.
 * 
 * @author jbercic
 *
 */
public final class Trigonometry {
	
	private static double [] cos_array;
	private static double [] sin_array;
	
	/**
	 * Initializes the lookup tables for cosine and sine functions.
	 */
	static {
		int i;
		double trnt=0.0;
		
		cos_array=new double[36000]; 
		sin_array=new double[36000];
		
		for (i=0;i<36000;i++) {
			cos_array[i]=Math.cos(Math.toRadians(trnt));
			sin_array[i]=Math.sin(Math.toRadians(trnt));
			trnt+=0.01;
		}
	}
	
	public static double cos(double angle) {
		if (angle<0) {
			return cos_array[((int)(-angle*100))%36000];
		}
		return cos_array[((int)(angle*100))%36000];
	}
	
	public static double sin(double angle) {
		if (angle<0) {
			return -sin_array[((int)(-angle*100))%36000];
		}
		return sin_array[((int)(angle*100))%36000];
	}
}
