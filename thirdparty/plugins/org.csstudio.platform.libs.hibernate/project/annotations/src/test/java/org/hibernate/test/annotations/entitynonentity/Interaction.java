//$Id: Interaction.java 15049 2008-08-13 15:32:32Z epbernard $
package org.hibernate.test.annotations.entitynonentity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;

/**
 * @author Emmanuel Bernard
 */
@MappedSuperclass
public class Interaction {
	@Column(name="int_nbr")
	public int number;
}
