//$Id: Manufacturer.java 18869 2010-02-24 17:11:05Z hardy.ferentschik $
package org.hibernate.test.annotations.onetomany;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Emmanuel Bernard
 */
public class Manufacturer {
	private Integer id;
	private Set<Model> models = new HashSet<Model>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Model> getModels() {
		return models;
	}

	public void setModels(Set<Model> models) {
		this.models = models;
	}
}