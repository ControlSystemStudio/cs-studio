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

@Table(name = "artikel_eingebaut")
@NamedQueries({
	@NamedQuery(name = ArticleInstalled.FIND_RECORD, query = "from ArticleInstalled a where a.artikelDatenId = :artikelDatenId order by id desc"),
	@NamedQuery(name = ArticleInstalled.FIND_INSTALLED_IN, query = "from ArticleInstalled a where a.eingebautInArtikel = :eingebautInArtikel")}
)
@Entity
public class ArticleInstalled extends BindingEntity implements ArticleHistoryInfo {
		
	private static final long serialVersionUID = 1L;

	public static final String FIND_RECORD = "ArticleInstalled.findRecord";
	public static final String FIND_INSTALLED_IN = "ArticleInstalled.findInstalledIn";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_ARTIKEL_EINGEBAUT")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;
	
	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;
	
	@Column(name = "projekt")
	@Size(max = 20)
	private String project;

	@Column(name = "device")
	@Size(max = 20)
	private String device;

	@Column(name = "gebaeude")
	@Size(max = 20)
	private String gebaeude;

	@Column(name = "raum")
	@Size(max = 20)
	private String raum;

	@Column(name = "eingebaut_in_artikel")
	private BigDecimal eingebautInArtikel;

	@Column(name = "eingebaut_am")
	@NotNull
	private Date eingebautAm;

	@Column(name = "eingebaut_durch")
	@Size(max = 30)
	@NotEmpty
	private String eingebautDurch;

	@Column(name = "location_details")
	@Size(max = 20)
	private String locationDetails;

	@Column(name = "raum_id")
	private BigDecimal raumId;
	
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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		pcs.firePropertyChange("project", this.project, project);
		this.project = project;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		pcs.firePropertyChange("device", this.device, device);
		this.device = device;
	}

	public String getGebaeude() {
		return gebaeude;
	}

	public void setGebaeude(String gebaeude) {
		pcs.firePropertyChange("gebaeude", this.gebaeude, gebaeude);
		this.gebaeude = gebaeude;
	}

	public String getRaum() {
		return raum;
	}

	public void setRaum(String raum) {
		pcs.firePropertyChange("raum", this.raum, raum);
		this.raum = raum;
	}

	public BigDecimal getEingebautInArtikel() {
		return eingebautInArtikel;
	}

	public void setEingebautInArtikel(BigDecimal eingebautInArtikel) {
		pcs.firePropertyChange("eingebautInArtikel", this.eingebautInArtikel, eingebautInArtikel);
		this.eingebautInArtikel = eingebautInArtikel;
	}

	public Date getEingebautAm() {
		return eingebautAm;
	}

	public void setEingebautAm(Date eingebautAm) {
		pcs.firePropertyChange("eingebautAm", this.eingebautAm, eingebautAm);
		this.eingebautAm = eingebautAm;
	}

	public String getEingebautDurch() {
		return eingebautDurch;
	}

	public void setEingebautDurch(String eingebautDurch) {
		pcs.firePropertyChange("eingebautDurch", this.eingebautDurch, eingebautDurch);
		this.eingebautDurch = eingebautDurch;
	}

	public String getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(String locationDetails) {
		this.locationDetails = locationDetails;
	}

	public BigDecimal getRaumId() {
		return raumId;
	}

	public void setRaumId(BigDecimal raumId) {
		pcs.firePropertyChange("raumId", this.raumId, raumId);
		this.raumId = raumId;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public Date getDate() {
		return eingebautAm;
	}

	@Override
	public String getStatusDescritpion() {
		return "Eingebaut";
	}
	
}
