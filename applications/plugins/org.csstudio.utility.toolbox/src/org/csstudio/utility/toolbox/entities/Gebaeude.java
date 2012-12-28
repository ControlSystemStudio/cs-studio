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

import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "Gebaeude")
@NamedQueries({ @NamedQuery(name = Gebaeude.FIND_ALL, query = "select g from Gebaeude g order by g.name"),
			@NamedQuery(name = Gebaeude.FIND_BY_NAME, query = "select g from Gebaeude g where name = :name") })
@Entity
public class Gebaeude implements TextValue {

	public static final String FIND_ALL = "Gebaeude.findAll";
	public static final String FIND_BY_NAME = "Gebaeude.findByName";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GA")
	@SequenceGenerator(name = "GA", sequenceName = "SEQ_GEBAEUDE")
	@Column(name = "gebaeude_id")
	private BigDecimal gebaeudeId;

	@Column(name = "name")
	@NotEmpty
	private String name;

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
