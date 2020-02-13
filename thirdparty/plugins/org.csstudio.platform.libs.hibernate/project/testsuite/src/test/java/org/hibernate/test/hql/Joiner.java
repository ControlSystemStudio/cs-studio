// $Id: Joiner.java 6021 2005-03-06 02:02:30Z steveebersole $
package org.hibernate.test.hql;

/**
 * Implementation of Joiner.
 *
 * @author Steve Ebersole
 */
public class Joiner {
	private Long id;
	private String name;
	private String joinedName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJoinedName() {
		return joinedName;
	}

	public void setJoinedName(String joinedName) {
		this.joinedName = joinedName;
	}
}
