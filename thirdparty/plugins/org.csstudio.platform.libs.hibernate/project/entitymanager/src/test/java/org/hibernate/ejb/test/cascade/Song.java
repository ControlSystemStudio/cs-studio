//$Id: Song.java 16383 2009-04-21 14:02:32Z jcosta@redhat.com $
package org.hibernate.ejb.test.cascade;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Song {
	@Id @GeneratedValue
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Author author;

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
