package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "artikel_eingang")
@NamedQueries({
	@NamedQuery(name = ArticleDelivered.FIND_RECORD, query = "from ArticleDelivered a where a.artikelDatenId = :artikelDatenId order by id desc")}
)
@Entity
public class ArticleDelivered extends BindingEntity implements ArticleHistoryInfo {
	
	private static final long serialVersionUID = -1;

	public static final String FIND_RECORD = "ArticleDelivered.findRecord";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_ARTIKEL_EINGANG")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;
	
	@Column(name = "eingetragen_durch")
	@Size(max = 30)
	@NotEmpty
	private String eingegangenDurch;
	
	@Column(name = "eingegangen_am")
	@NotNull
	private Date eingegangenAm;
	
	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getArtikelDatenId() {
		return artikelDatenId;
	}

	public void setArtikelDatenId(BigDecimal artikelDatenId) {
		this.artikelDatenId = artikelDatenId;
	}

	public String getEingegangenDurch() {
		return eingegangenDurch;
	}

	public void setEingegangenDurch(String eingegangenDurch) {
		pcs.firePropertyChange("eingegangenDurch", this.eingegangenDurch, eingegangenDurch);
		this.eingegangenDurch = eingegangenDurch;
	}

	public Date getEingegangenAm() {
		return eingegangenAm;
	}

	public void setEingegangenAm(Date eingegangenAm) {
		pcs.firePropertyChange("eingegangenAm", this.eingegangenAm, eingegangenAm);
		this.eingegangenAm = eingegangenAm;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public Date getDate() {
		return eingegangenAm;
	}

	@Override
	public String getStatusDescritpion() {
		return "Eingang";
	}
	
}
