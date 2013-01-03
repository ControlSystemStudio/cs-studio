package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Entity
@NamedQueries({ @NamedQuery(name = LagerOrt.FIND_ALL, query = "from LagerOrt l where l.lagerName = :lagerName order by l.name"),
			@NamedQuery(name = LagerOrt.FIND_BY_NAME, query = "from LagerOrt l where l.lagerName = :lagerName and name = :name") })
@Table(name = "lager_ort")
public class LagerOrt implements TextValue {

	public static final String FIND_ALL = "LagerOrt.findAll";
	public static final String FIND_BY_NAME = "LagerOrt.findByName";

	@Id
	@Size(max = 20)
	@Column(name = "name")
	private String name;

	@Column(name = "lager_name")
	@Size(max = 20)
	private String lagerName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLagerName() {
		return lagerName;
	}

	public void setLagerName(String lagerName) {
		this.lagerName = lagerName;
	}

	@Override
	public String getValue() {
		return name;
	}

}