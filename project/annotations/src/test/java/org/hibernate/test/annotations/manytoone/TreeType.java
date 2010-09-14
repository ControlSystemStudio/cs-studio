//$Id: TreeType.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.JoinTable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class TreeType {
	private Integer id;
	private String name;
	private ForestType forestType;
	private ForestType alternativeForestType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name="Tree_Forest")
	public ForestType getForestType() {
		return forestType;
	}

	public void setForestType(ForestType forestType) {
		this.forestType = forestType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name="Atl_Forest_Type",
		joinColumns = @JoinColumn(name="tree_id"),
		inverseJoinColumns = @JoinColumn(name="forest_id") )
	public ForestType getAlternativeForestType() {
		return alternativeForestType;
	}

	public void setAlternativeForestType(ForestType alternativeForestType) {
		this.alternativeForestType = alternativeForestType;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
