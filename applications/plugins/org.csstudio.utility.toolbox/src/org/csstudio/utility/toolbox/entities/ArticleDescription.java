package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;

@Table(name = "artikel_beschreibung")
@NamedQueries({ @NamedQuery(name = ArticleDescription.FIND_ALL, query = "from ArticleDescription ad order by ad.beschreibung") })
@Entity
public class ArticleDescription extends BindingEntity implements Cloneable<ArticleDescription> {

	private static final long serialVersionUID = -1L;

	public static final String FIND_ALL = "ArticleDescription.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GAD")
	@SequenceGenerator(name = "GAD", sequenceName = "SEQ_ARTIKEL_BESCHREIBUNG")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Size(max = 8)
	@Column(name = "dfg_schluessel")
	private String dfgSchluessel;

	@NotNull
	@Size(max = 200)
	@Column(name = "beschreibung")
	private String beschreibung;

	@Size(max = 30)
	@Column(name = "produkt_typ")
	private String produktTyp;

	@Size(max = 30)
	@Column(name = "lieferant_name")
	private String lieferantName;

	@Size(max = 20)
	@Column(name = "lieferant_best_nr")
	private String lieferantBestNr;

	@Column(name = "lieferant_stueckpreis")
	private BigDecimal lieferantStueckpreis;

	@Size(max = 120)
	@Column(name = "html_link")
	private String htmlLink;

	@Column(name = "vergleichs_typ")
	private BigDecimal vergleichsTyp;

	@Column(name = "desy_lager_nr")
	private BigDecimal desyLagerNr;

	@Column(name = "lieferzeit_wochen")
	private BigDecimal lieferzeitWochen;

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	@PostPersist
	@PostUpdate
	protected void recordChanged() {
		pcs.firePropertyChange("id", null, id);
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getDfgSchluessel() {
		return dfgSchluessel;
	}

	public void setDfgSchluessel(String dfgSchluessel) {
		pcs.firePropertyChange("dfgSchluessel", this.dfgSchluessel, dfgSchluessel);
		this.dfgSchluessel = dfgSchluessel;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	@Transient
	public String getBeschreibung(int maxLength) {
		if (beschreibung.length() > maxLength) {
			return beschreibung.substring(0, maxLength);
		}
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		pcs.firePropertyChange("beschreibung", this.beschreibung, beschreibung);
		this.beschreibung = beschreibung;
	}

	public String getProduktTyp() {
		return produktTyp;
	}

	public void setProduktTyp(String produktTyp) {
		pcs.firePropertyChange("produktTyp", this.produktTyp, produktTyp);
		this.produktTyp = produktTyp;
	}

	public String getLieferantName() {
		return lieferantName;
	}

	public void setLieferantName(String lieferantName) {
		pcs.firePropertyChange("lieferantName", this.lieferantName, lieferantName);
		this.lieferantName = lieferantName;
	}

	public String getLieferantBestNr() {
		return lieferantBestNr;
	}

	public void setLieferantBestNr(String lieferantBestNr) {
		pcs.firePropertyChange("lieferantBestNr", this.lieferantBestNr, lieferantBestNr);
		this.lieferantBestNr = lieferantBestNr;
	}

	public BigDecimal getLieferantStueckpreis() {
		return lieferantStueckpreis;
	}

	public void setLieferantStueckpreis(BigDecimal lieferantStueckpreis) {
		pcs.firePropertyChange("lieferant_stueckpreis", this.lieferantStueckpreis, lieferantStueckpreis);
		this.lieferantStueckpreis = lieferantStueckpreis;
	}

	public String getHtmlLink() {
		return htmlLink;
	}

	public void setHtmlLink(String htmlLInk) {
		pcs.firePropertyChange("htmlLink", this.htmlLink, htmlLInk);
		this.htmlLink = htmlLInk;
	}

	public BigDecimal getVergleichsTyp() {
		return vergleichsTyp;
	}

	public void setVergleichsTyp(BigDecimal vergleichsTyp) {
		pcs.firePropertyChange("vergleichsTyp", this.vergleichsTyp, vergleichsTyp);
		this.vergleichsTyp = vergleichsTyp;
	}

	public BigDecimal getDesyLagerNr() {
		return desyLagerNr;
	}

	public void setDesyLagerNr(BigDecimal desyLagerNr) {
		pcs.firePropertyChange("desyLagerNr", this.desyLagerNr, desyLagerNr);
		this.desyLagerNr = desyLagerNr;
	}

	public BigDecimal getLieferzeitWochen() {
		return lieferzeitWochen;
	}

	public void setLieferzeitWochen(BigDecimal lieferzeitWochen) {
		pcs.firePropertyChange("lieferzeit_wochen", this.lieferzeitWochen, lieferzeitWochen);
		this.lieferzeitWochen = lieferzeitWochen;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return beschreibung;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		ArticleDescription rhs = (ArticleDescription) obj;

		return new EqualsBuilder().append(id, rhs.id).isEquals();
	}

	@Override
	public ArticleDescription deepClone() {
		ArticleDescription clone = new ArticleDescription();
		clone.id = this.id;
		clone.dfgSchluessel = dfgSchluessel;
		clone.beschreibung = beschreibung;
		clone.produktTyp = produktTyp;
		clone.lieferantName = lieferantName;
		clone.lieferantBestNr = lieferantBestNr;
		clone.lieferantStueckpreis = lieferantStueckpreis;
		clone.htmlLink = htmlLink;
		clone.vergleichsTyp = vergleichsTyp;
		clone.desyLagerNr = desyLagerNr;
		clone.lieferzeitWochen = lieferzeitWochen;
		return clone;
	}

}
