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

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "artikel_in_lager")
@NamedQueries({
	@NamedQuery(name = ArticleInStore.FIND_RECORD, query = "from ArticleInStore a where a.artikelDatenId = :artikelDatenId order by id desc")}
)
@Entity
public class ArticleInStore  extends BindingEntity implements ArticleHistoryInfo {
	
	private static final long serialVersionUID = 1L;

	public static final String FIND_RECORD = "ArticleInStore.findRecord";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_ARTIKEL_IN_LAGER")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Column(name = "lager_name")
	@NotNull
	private String lagerName;

	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;

	@Column(name = "lager_artikel_id")
	@Size(max=30)
	@NotNull
	private String lagerArtikelId;

	@Column(name = "in_lager_am")
	@NotNull
	private Date inLagerAm;
	
	@Column(name = "in_lager_durch")
	@Size(max=30)
	@NotEmpty
	private String inLagerDurch;

	@Column(name = "flag_exist")
	@Size(max=3)
	private String flagExist;

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

	public String getLagerName() {
		return lagerName;
	}

	public void setLagerName(String lagerName) {
		this.lagerName = lagerName;
	}

	public BigDecimal getArtikelDatenId() {
		return artikelDatenId;
	}

	public void setArtikelDatenId(BigDecimal artikelDatenId) {
		this.artikelDatenId = artikelDatenId;
	}

	public String getLagerArtikelId() {
		return lagerArtikelId;
	}

	public void setLagerArtikelId(String lagerArtikelId) {
		this.lagerArtikelId = lagerArtikelId;
	}

	public Date getInLagerAm() {
		return inLagerAm;
	}

	public void setInLagerAm(Date inLagerAm) {
		this.inLagerAm = inLagerAm;
	}

	public String getInLagerDurch() {
		return inLagerDurch;
	}

	public void setInLagerDurch(String imLagerDurch) {
		this.inLagerDurch = imLagerDurch;
	}

	public String getFlagExist() {
		return flagExist;
	}

	public void setFlagExist(String flagExist) {
		this.flagExist = flagExist;
	}

	@Override
	public Date getDate() {
		return inLagerAm;
	}

	@Override
	public String getStatusDescritpion() {
		return "In Lager";
	}
	
}
