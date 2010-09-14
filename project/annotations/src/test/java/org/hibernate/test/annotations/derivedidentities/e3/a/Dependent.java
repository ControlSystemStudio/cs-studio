package org.hibernate.test.annotations.derivedidentities.e3.a;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
@IdClass(DependentId.class)
public class Dependent {
	@Id
	@Column(name = "dep_name")
	String name; // default column name is overridden
	
	@Id
	@JoinColumns({
			@JoinColumn(name = "FK1", referencedColumnName = "firstName"),
			@JoinColumn(name = "FK2", referencedColumnName = "lastName")
	})
	@ManyToOne
	Employee emp;
}
