//$Id: City.java 15046 2008-08-13 14:59:47Z epbernard $
package org.hibernate.test.annotations.onetomany;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;

/**
 * @author Emmanuel Bernard
 */
@Entity
class City {
	private Integer id;
	private String name;
	private List<Street> streets;
	private List<Street> mainStreets;

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

	@OneToMany(mappedBy = "city")
	@OrderBy("streetNameCopy, id")
	public synchronized List<Street> getStreets() {
		return streets;
	}

	public void setStreets(List<Street> streets) {
		this.streets = streets;
	}

	@OneToMany()
	@JoinColumn(name = "mainstreetcity_id")
	@ForeignKey(name = "CITYSTR_FK")
	@OrderBy
	@Immutable
	public List<Street> getMainStreets() {
		return mainStreets;
	}

	public void setMainStreets(List<Street> streets) {
		this.mainStreets = streets;
	}

	public void addMainStreet(Street street) {
		if ( mainStreets == null ) mainStreets = new ArrayList<Street>();
		mainStreets.add( street );
	}

}
