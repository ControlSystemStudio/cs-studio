package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@NamedQueries({
			@NamedQuery(name = LagerArtikel.FIND_ALL, query = "from LagerArtikel where lagerName = :name and articleDescription.id = :id"),
			@NamedQuery(name = LagerArtikel.FIND_BY_ID, query = "from LagerArtikel where id = :id") })
@Table(name = "lager_artikel")
public class LagerArtikel extends BindingEntity implements TextValue {

	private static final long serialVersionUID = 1L;
	
	public static final String FIND_ALL = "LagerArtikel.findAll";
	public static final String FIND_BY_ID = "LagerArtikel.findById";

	@Id
	@Size(max = 30)
	@NotEmpty
	private String id;

	@Column(name = "lager_name")
	@Size(max = 20)
	@NotEmpty
	private String lagerName;

	@Column(name = "ort")
	@Size(max = 20)
	private String ort;

	@Column(name = "fach")
	@Size(max = 20)
	private String fach;

	@Column(name = "box")
	@Size(max = 20)
	private String box;

	@Column(name = "soll_bestand")
	private BigDecimal sollBestand;

	@Column(name = "actual_bestand")
	private BigDecimal actualBestand;

	@Column(name = "quantity")
	@Size(max = 8)
	private String quantity = "Stk";

	@Column(name = "note")
	@Size(max = 200)
	private String note;

	@OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "artikel_beschreibung_id", referencedColumnName = "id")
	@Valid
	private ArticleDescription articleDescription;
	
	@PostPersist
	@PostUpdate
	protected void recordChanged() {
		pcs.firePropertyChange("id", null, id);
	}

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Transient
	@ReadOnly
	public String getBeschreibung() {
		if (articleDescription == null) {
			return "";
		} else {
			return articleDescription.getBeschreibung();
		}
	}

	public String getLagerName() {
		return lagerName;
	}

	public void setLagerName(String lagerName) {
		pcs.firePropertyChange("lagerName", this.lagerName, lagerName);
		this.lagerName = lagerName;
	}

	@Transient
	public BigDecimal getArtikelBeschreibungId() {
		return articleDescription.getId();
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		pcs.firePropertyChange("ort", this.ort, ort);
		this.ort = ort;
	}

	public String getFach() {
		return fach;
	}

	public void setFach(String fach) {
		pcs.firePropertyChange("fach", this.fach, fach);
		this.fach = fach;
	}

	public String getBox() {
		return box;
	}

	public void setBox(String box) {
		pcs.firePropertyChange("box", this.box, box);
		this.box = box;
	}

	public BigDecimal getSollBestand() {
		return sollBestand;
	}

	public void setSollBestand(BigDecimal sollBestand) {
		pcs.firePropertyChange("sollBestand", this.sollBestand, sollBestand);
		this.sollBestand = sollBestand;
	}

	public BigDecimal getActualBestand() {
		return actualBestand;
	}

	public void setActualBestand(BigDecimal actualBestand) {
		this.actualBestand = actualBestand;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		pcs.firePropertyChange("note", this.note, note);
		this.note = note;
	}

	public ArticleDescription getArticleDescription() {
		return articleDescription;
	}

	public void setArticleDescription(ArticleDescription articleDescription) {
		pcs.firePropertyChange("articleDescription", this.articleDescription, articleDescription);
		this.articleDescription = articleDescription;
	}

	@Override
	public String getValue() {
		return id;
	}

}
