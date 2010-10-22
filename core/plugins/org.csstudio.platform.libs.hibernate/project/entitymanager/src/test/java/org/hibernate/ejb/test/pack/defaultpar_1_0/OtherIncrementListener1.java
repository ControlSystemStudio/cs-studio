//$Id: OtherIncrementListener1.java 18259 2009-12-17 15:34:04Z epbernard $
package org.hibernate.ejb.test.pack.defaultpar_1_0;

/**
 * @author Emmanuel Bernard
 */
public class OtherIncrementListener1 {
	private static int increment;

	public static int getIncrement() {
		return OtherIncrementListener1.increment;
	}

	public static void reset() {
		increment = 0;
	}

	public void increment(Object entity) {
		OtherIncrementListener1.increment++;
	}
}