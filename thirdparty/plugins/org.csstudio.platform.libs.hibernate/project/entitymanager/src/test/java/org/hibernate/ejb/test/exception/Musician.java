// $Id: Musician.java 19255 2010-04-21 01:57:44Z steve.ebersole@jboss.com $
package org.hibernate.ejb.test.exception;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Hardy Ferentschik
 */
@Entity
@SuppressWarnings("serial")
public class Musician implements Serializable {
	private Integer id;
	
	private String name;
	
	private Music favouriteMusic;

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

	@ManyToOne
	public Music getFavouriteMusic() {
		return favouriteMusic;
	}

	public void setFavouriteMusic(Music favouriteMusic) {
		this.favouriteMusic = favouriteMusic;
	}
}
