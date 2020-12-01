//$Id: Tomato.java 15056 2008-08-13 18:15:05Z epbernard $
package org.hibernate.test.annotations.inheritance;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Column;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Emmanuel Bernard
 */
//FIXME HBX-55 default for composite PK does not work yet
//FIXME Tomato is a fruit
@Entity()
@Inheritance(
		strategy = InheritanceType.JOINED
)
@OnDelete(action = OnDeleteAction.CASCADE)
public class Tomato extends Vegetable {
	private int size;

	@Column(name="tom_size")
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
