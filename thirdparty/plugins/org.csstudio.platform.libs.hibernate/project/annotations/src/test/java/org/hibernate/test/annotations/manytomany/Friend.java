//$Id: Friend.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.manytomany;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


/**
 * Friend has other friends in a many to many way
 *
 * @author Emmanuel Bernard
 */
@Entity()
public class Friend implements Serializable {
	private Integer id;
	private String name;
	private Set<Friend> friends;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer integer) {
		id = integer;
	}

	public void setName(String string) {
		name = string;
	}

	@ManyToMany(
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	@JoinTable(
			name = "FRIEND2FRIEND",
			joinColumns = {@JoinColumn(name = "FROM_FR", nullable = false)},
			inverseJoinColumns = {@JoinColumn(name = "TO_FR", nullable = false)}
	)
	public Set<Friend> getFriends() {
		return friends;
	}

	public void setFriends(Set<Friend> friend) {
		this.friends = friend;
	}
}
