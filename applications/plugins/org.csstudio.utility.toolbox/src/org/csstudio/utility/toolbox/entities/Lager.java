package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Entity
@NamedQueries({ @NamedQuery(name = Lager.FIND_ALL, query = "select l from Lager l order by l.name"),
			@NamedQuery(name = Lager.FIND_BY_NAME, query = "select l from Lager l where l.name = :name") })
@Table(name = "lager")
public class Lager extends BindingEntity implements Cloneable<Lager>, TextValue {

	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "Lager.findAll";

	public static final String FIND_BY_NAME = "Lager.findByName";

	@Size(max = 20)
	@Id
	private String name;

	@Size(max = 10)
	@Column(name = "group_owner")
	private String groupOwner;

	@Size(max = 30)
	@Column(name = "responsible_person")
	private String responsiblePerson;

	@Size(max = 20)
	@Column(name = "in_gebaeude")
	private String inGebaeude;

	@Size(max = 20)
	@Column(name = "in_raum")
	private String inRaum;

	@Size(max = 10)
	@Column(name = "lager_praefix")
	private String lagerPrefix;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		pcs.firePropertyChange("name", this.name, name);
		this.name = name;
	}

	public String getGroupOwner() {
		return groupOwner;
	}

	public void setGroupOwner(String groupOwner) {
		pcs.firePropertyChange("groupOwner", this.groupOwner, groupOwner);
		this.groupOwner = groupOwner;
	}

	public String getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(String responsiblePerson) {
		pcs.firePropertyChange("responsiblePerson", this.responsiblePerson, responsiblePerson);
		this.responsiblePerson = responsiblePerson;
	}

	public String getInGebaeude() {
		return inGebaeude;
	}

	public void setInGebaeude(String inGebaeude) {
		pcs.firePropertyChange("inGebaeude", this.inGebaeude, inGebaeude);
		this.inGebaeude = inGebaeude;
	}

	public String getInRaum() {
		return inRaum;
	}

	public void setInRaum(String inRaum) {
		pcs.firePropertyChange("inRaum", this.inRaum, inRaum);
		this.inRaum = inRaum;
	}

	public String getLagerPrefix() {
		return lagerPrefix;
	}

	public void setLagerPrefix(String lagerPrefix) {
		pcs.firePropertyChange("lagerPrefix", this.lagerPrefix, lagerPrefix);
		this.lagerPrefix = lagerPrefix;
	}

	@Override
	public String getValue() {
		return name;
	}

	@Override
	public Lager deepClone() {
		Lager clone = new Lager();
		clone.name = null;
		clone.groupOwner = groupOwner;
		clone.responsiblePerson = responsiblePerson;
		clone.inGebaeude = inGebaeude;
		clone.inRaum = inRaum;
		clone.lagerPrefix = lagerPrefix;
		return clone;
	}

}
