package org.hibernate.ejb.test.metadata;

import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class DeskWithRawType implements java.io.Serializable {

	@Id
	protected String id;

	@Basic
	protected String name;

	public DeskWithRawType() {
	}

	@ManyToMany(targetEntity = EmployeeWithRawType.class, cascade = CascadeType.ALL)
	@JoinTable(name = "DESK_EMPL",
			joinColumns =
			@JoinColumn(
					name = "DESK_FK", referencedColumnName = "ID"),
			inverseJoinColumns =
			@JoinColumn(
					name = "EMPL_FK", referencedColumnName = "ID")
	)
	protected Collection employees = new java.util.ArrayList();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection getEmployees() {
		return employees;
	}

	public void setEmployees(Collection employees) {
		this.employees = employees;
	}
}
