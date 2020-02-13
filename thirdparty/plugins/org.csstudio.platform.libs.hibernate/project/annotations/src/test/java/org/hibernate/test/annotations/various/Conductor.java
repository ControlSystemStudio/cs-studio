//$Id: Conductor.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.various;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OptimisticLock;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Conductor {
	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "cond_name")
	@Index(name = "cond_name")
	@OptimisticLock(excluded = true)
	private String name;

	@Version
	private Long version;


	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

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
