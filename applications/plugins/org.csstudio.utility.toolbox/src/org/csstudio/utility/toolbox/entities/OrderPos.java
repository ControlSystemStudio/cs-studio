package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;

@Table(name = "BA_POSITION")
@NamedQueries({
	@NamedQuery(name = OrderPos.FIND_IN_ARTIKEL_DATEN_ID, query = "from OrderPos o where o.artikelDatenId = :artikelDatenId"),
	@NamedQuery(name = OrderPos.FIND_BY_BA_NR, query = "from OrderPos o where o.baNr = :baNr") })
@Entity
public class OrderPos extends BindingEntity implements Cloneable<OrderPos> {

	public static final String FIND_IN_ARTIKEL_DATEN_ID = "OrderPos.findArtikelDatenId";
	
	public static final String FIND_BY_BA_NR = "OrderPos.findByBaNr";
	
	private static final long serialVersionUID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_BA_POSITION")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Column(name = "POSITION_NR")
	private BigDecimal positionNr;

	@Column(name = "ANZAHL_BESTELLT")
	private BigDecimal anzahlBestellt;

	@Column(name = "ANZAHL_GELIEFERT")
	private BigDecimal anzahlGeliefert;

	@Column(name = "LIEFERDATUM_GEPLANT")
	private Date lieferDatumGeplant;

	@Column(name = "LIEFERDATUM")
	private Date lieferDatum;

	@Column(name = "EINZELPREIS")
	private BigDecimal einzelPreis;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BA_NR", referencedColumnName = "NUMMER", insertable = false, updatable = false)
	private Order order;

	@Column(name = "ba_nr")
	private BigDecimal baNr;

	@Column(name = "artikel_daten_id", insertable = false, updatable = false)
	private BigDecimal artikelDatenId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "artikel_daten_id", referencedColumnName = "id")
	private Article article;

	public static OrderPos buildNewOrderPos(BigDecimal positionNr) {
		OrderPos orderPos = new OrderPos();
		orderPos.article = new Article();
		orderPos.article.setStatus("nicht definiert");
		ArticleDescription ad = new ArticleDescription();
		ad.setBeschreibung("");
		orderPos.article.setArticleDescription(ad);
		orderPos.anzahlBestellt = BigDecimal.ONE;
		orderPos.positionNr = positionNr;
		return orderPos;
	}

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	public Order getOrder() {
		return order;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public Article getArticle() {
		return article;
	}

	public BigDecimal getPositionNr() {
		return positionNr;
	}

	public void setPositionNr(BigDecimal positionNr) {
		this.positionNr = positionNr;
	}

	public BigDecimal getAnzahlBestellt() {
		return anzahlBestellt;
	}

	public void setAnzahlBestellt(BigDecimal anzahlBestellt) {
		this.anzahlBestellt = anzahlBestellt;
		if (order != null) {
			order.childsHaveChanged();
		}
	}

	public BigDecimal getAnzahlGeliefert() {
		return anzahlGeliefert;
	}

	public void setAnzahlGeliefert(BigDecimal anzahlGeliefert) {
		this.anzahlGeliefert = anzahlGeliefert;
		if (order != null) {
			order.childsHaveChanged();
		}
	}

	public Date getLieferDatumGeplant() {
		return lieferDatumGeplant;
	}

	public void setLieferDatumGeplant(Date lieferDatumGeplant) {
		this.lieferDatumGeplant = lieferDatumGeplant;
	}

	public Date getLieferDatum() {
		return lieferDatum;
	}

	public void setLieferDatum(Date lieferDatum) {
		this.lieferDatum = lieferDatum;
	}

	public BigDecimal getEinzelPreis() {
		return einzelPreis;
	}

	public void setEinzelPreis(BigDecimal einzelPreis) {
		this.einzelPreis = einzelPreis;
		if (order != null) {
			order.childsHaveChanged();
		}
	}

	public BigDecimal getArtikelDatenId() {
		return artikelDatenId;
	}

	public void setArtikelDatenId(BigDecimal artikelDatenId) {
		this.artikelDatenId = artikelDatenId;
	}

	public void setArticleDescription(ArticleDescription articleDescription) {
		getArticle().setArticleDescription(articleDescription);
		if (order != null) {
			order.childsHaveChanged();
		}
	}

	public BigDecimal getBaNr() {
		return baNr;
	}

	public void setBaNr(BigDecimal baNr) {
		this.baNr = baNr;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public OrderPos deepClone() {
		try {
			OrderPos clone = new OrderPos();
			clone.positionNr = positionNr;
			clone.anzahlBestellt = anzahlBestellt;
			clone.anzahlGeliefert = anzahlGeliefert;
			clone.lieferDatumGeplant = lieferDatumGeplant;
			clone.lieferDatum = lieferDatum;
			clone.einzelPreis = einzelPreis;
			clone.id = null;
			clone.article = this.article.deepClone();
			return clone;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
