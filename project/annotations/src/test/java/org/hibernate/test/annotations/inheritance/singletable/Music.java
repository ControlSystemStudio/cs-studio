//$Id: Music.java 16304 2009-04-11 04:43:19Z gbadner $
package org.hibernate.test.annotations.inheritance.singletable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DiscriminatorFormula;

/**
 * @author Emmanuel Bernard
 */
@Entity
@DiscriminatorColumn(discriminatorType=DiscriminatorType.INTEGER)
@DiscriminatorFormula("case when zik_type is null then 0 else zik_type end")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"avgBeat", "starred"} ))
public abstract class Music {
	private Integer id;
	private int avgBeat;
	private Integer type;

	@Column(name = "zik_type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getAvgBeat() {
		return avgBeat;
	}

	public void setAvgBeat(int avgBeat) {
		this.avgBeat = avgBeat;
	}
}
