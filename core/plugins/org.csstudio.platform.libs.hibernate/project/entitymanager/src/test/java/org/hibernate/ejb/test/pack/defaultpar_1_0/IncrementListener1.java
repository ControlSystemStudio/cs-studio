//$Id: IncrementListener1.java 18259 2009-12-17 15:34:04Z epbernard $
package org.hibernate.ejb.test.pack.defaultpar_1_0;

import javax.persistence.PrePersist;

/**
 * @author Emmanuel Bernard
 */
public class IncrementListener1 {
	private static int increment;

	public static int getIncrement() {
		return increment;
	}

	public static void reset() {
		increment = 0;
	}

	@PrePersist
	public void increment(Object entity) {
		increment++;
	}
}