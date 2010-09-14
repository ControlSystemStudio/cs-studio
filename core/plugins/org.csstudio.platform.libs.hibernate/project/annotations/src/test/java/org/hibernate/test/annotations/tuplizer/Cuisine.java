//$Id: Cuisine.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.tuplizer;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Tuplizer;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Tuplizer(impl = DynamicEntityTuplizer.class)
public interface Cuisine {
	@Id
	@GeneratedValue
	public Long getId();
	public void setId(Long id);

	public String getName();
	public void setName(String name);

	@Tuplizer(impl = DynamicComponentTuplizer.class)
	public Country getCountry();
	public void setCountry(Country country);


}
