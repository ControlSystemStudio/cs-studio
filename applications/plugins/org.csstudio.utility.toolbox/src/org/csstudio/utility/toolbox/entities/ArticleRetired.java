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

@Table(name = "artikel_ausgemustert")
@NamedQueries({
	@NamedQuery(name = ArticleRetired.FIND_RECORD, query = "from ArticleRetired a where a.artikelDatenId = :artikelDatenId order by id desc")}
)
@Entity
public class ArticleRetired extends BindingEntity implements ArticleHistoryInfo  {

	private static final long serialVersionUID = -1;
	
	public static final String FIND_RECORD = "ArticleRetired.findRecord";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_ARTIKEL_AUSGEMUSTERT")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;
	
	@Column(name = "ausgemustert_durch")
	@Size(max = 30)
	@NotEmpty
	private String ausgemustertDurch;

	@Column(name = "ausgemustert_am")
	@NotNull
	private Date ausgemustertAm;

	@Column(name = "begruendung")
	@Size(max = 200)
	private String begruendung;
	
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
	
	public String getAusgemustertDurch() {
		return ausgemustertDurch;
	}

	public void setAusgemustertDurch(String ausgemustertDurch) {
		pcs.firePropertyChange("ausgemustertDurch", this.ausgemustertDurch, ausgemustertDurch);
		this.ausgemustertDurch = ausgemustertDurch;
	}

	public Date getAusgemustertAm() {
		return ausgemustertAm;
	}

	public void setAusgemustertAm(Date ausgemustertAm) {
		pcs.firePropertyChange("ausgemustertAm", this.ausgemustertAm, ausgemustertAm);
		this.ausgemustertAm = ausgemustertAm;
	}

	public String getBegruendung() {
		return begruendung;
	}

	public void setBegruendung(String begruendung) {
		pcs.firePropertyChange("begruendung", this.begruendung, begruendung);
		this.begruendung = begruendung;
	}

	@Override
	public Date getDate() {
		return ausgemustertAm;
	}

	@Override
	public String getStatusDescritpion() {
		return "Ausgemustert";
	}

}
