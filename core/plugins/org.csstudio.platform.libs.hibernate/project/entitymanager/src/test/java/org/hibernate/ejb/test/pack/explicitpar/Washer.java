//$Id: Washer.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.pack.explicitpar;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Washer {
	//No @id so picking it up should fail
}
