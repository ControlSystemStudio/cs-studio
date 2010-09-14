//$Id: ForestType.java 15074 2008-08-14 17:38:00Z epbernard $
package org.hibernate.test.annotations.manytoone;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.ForeignKey;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class ForestType {
	private Integer id;
	private String name;
	private Set<TreeType> trees;
	private BiggestForest biggestRepresentative;

	@OneToOne
	@JoinTable(name="BiggestRepPerForestType",
		joinColumns = @JoinColumn(name="forest_type"),
		inverseJoinColumns = @JoinColumn(name="forest")
	)
	@ForeignKey(name="A_TYP_FK",
			inverseName = "A_FOR_FK" //inverse fail cause it involves a Join
	)
	public BiggestForest getBiggestRepresentative() {
		return biggestRepresentative;
	}

	public void setBiggestRepresentative(BiggestForest biggestRepresentative) {
		this.biggestRepresentative = biggestRepresentative;
	}

	@OneToMany(mappedBy="forestType")
	public Set<TreeType> getTrees() {
		return trees;
	}

	public void setTrees(Set<TreeType> trees) {
		this.trees = trees;
	}

	@Id @GeneratedValue
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
