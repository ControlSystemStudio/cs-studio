package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "Raum")
@NamedQueries({
			@NamedQuery(name = Raum.FIND_ALL, query = "select r from Raum r where r.gebaeudeId = :gebaeudeId order by r.name"),
			@NamedQuery(name = Raum.FIND_BY_NAME_AND_GEBAUEDE_ID, query = "select r from Raum r where r.name = :name and r.gebaeudeId = :gebaeudeId ") })
@Entity
public class Raum implements TextValue {

	public static final String FIND_ALL = "Raum.findAll";
	public static final String FIND_BY_NAME_AND_GEBAUEDE_ID = "Raum.findByName";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GA")
	@SequenceGenerator(name = "GA", sequenceName = "SEQ_RAUM")
	@Column(name = "raum_id", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal raumId;

	@Column(name = "gebaeude_id")
	@NotNull
	private BigDecimal gebaeudeId;

	@Column(name = "name")
	@NotEmpty
	private String name;

	public BigDecimal getRaumId() {
		return raumId;
	}

	public void setRaumId(BigDecimal raumId) {
		this.raumId = raumId;
	}

	public BigDecimal getGebaeudeId() {
		return gebaeudeId;
	}

	public void setGebaeudeId(BigDecimal gebaeudeId) {
		this.gebaeudeId = gebaeudeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return name;
	}

}
