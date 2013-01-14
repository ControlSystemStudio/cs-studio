package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.framework.validator.ValidDate;

@Table(name = "BA")
@NamedQueries({ @NamedQuery(name = Order.FIND_ALL, query = "from Order l order by l.nummer desc") ,
	@NamedQuery(name = Order.FIND_BY_NUMMER, query = "from Order l where l.nummer = :nummer")})
@Entity
public class Order extends BindingEntity implements TextValue, Cloneable<Order> {

	public static final String FIND_ALL = "Order.findAll";
	public static final String FIND_BY_NUMMER = "Order.findByNummer";

	private static final long serialVersionUID = -1L;

	@Column(name = "nummer")
	@NotNull
	@Id
	private BigDecimal nummer;

	@Column(name = "firma_name")
	private String firmaName;

	@Column(name = "aussteller")
	private String aussteller;

	@Column(name = "gruppe")
	private String gruppe;

	@Column(name = "ablade_stelle")
	private String abladeStelle;

	@Column(name = "projekt")
	private String projekt;

	@Column(name = "desy_auftrags_nr")
	private String desyauftragsNr;

	@Column(name = "beschreibung")
	private String beschreibung;

	@Column(name = "text")
	private String text;

	@Column(name = "ba_type")
	private String baType;

	@Column(name = "kostenstelle")
	private String kostenstelle;

	@Column(name = "vorherige_ba")
	private BigDecimal vorherigeBa;

	@Column(name = "zu_inventar_nr")
	private BigDecimal zuInventarNr;

	@Column(name = "gesamtwert")
	private BigDecimal gesamtwert;

	@Column(name = "termin")
	@ValidDate
	private Date termin;

	@Column(name = "ausstellungs_datum")
	@ValidDate
	private Date austellungsDatum;

	@Column(name = "maintenance_contract")
	private Boolean maintenanceContract;

	@Column(name = "valid_until")
	@ValidDate
	private Date validUntil;

	@Column(name = "remember_expiration")
	private Boolean rememberExpiration;

	@Valid
	@Transient
	private List<OrderPos> orderPositions;

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	@PostPersist
	@PostUpdate
	protected void recordChanged() {
		setNewRecord(false);
		pcs.firePropertyChange("nummer", null, nummer);
	}

	public List<OrderPos> getOrderPositions(OrderPosFinder orderPosFinder) {
		if ((orderPositions == null)  && (nummer != null)) {
			orderPositions = orderPosFinder.findByBaNr(nummer);
		}
		return orderPositions;
	}

	public void setOrderPositions(List<OrderPos> orderPositions) {
		this.orderPositions = orderPositions;
	}

	public BigDecimal getNummer() {
		return nummer;
	}

	@Transient
	// the first digit contains the baType, which we do not want to show
	public String getBaNummer() {
		String baNummer = nummer.toString();
		if (StringUtils.isBlank(baNummer)) {
			return "";
		}
		return baNummer.substring(1);
	}

	public void setNummer(BigDecimal nummer) {
		pcs.firePropertyChange("nummer", this.nummer, nummer);
		this.nummer = nummer;
	}

	public String getFirmaName() {
		return firmaName;
	}

	public void setFirmaName(String firmaName) {
		pcs.firePropertyChange("firmaName", this.firmaName, firmaName);
		this.firmaName = firmaName;
	}

	public String getAussteller() {
		return aussteller;
	}

	public void setAussteller(String aussteller) {
		pcs.firePropertyChange("aussteller", this.aussteller, aussteller);
		this.aussteller = aussteller;
	}

	public String getGruppe() {
		return gruppe;
	}

	public void setGruppe(String gruppe) {
		pcs.firePropertyChange("gruppe", this.gruppe, gruppe);
		this.gruppe = gruppe;
	}

	public String getAbladeStelle() {
		return abladeStelle;
	}

	public void setAbladeStelle(String abladeStelle) {
		pcs.firePropertyChange("abladeStelle", this.abladeStelle, abladeStelle);
		this.abladeStelle = abladeStelle;
	}

	public String getProjekt() {
		return projekt;
	}

	public void setProjekt(String projekt) {
		pcs.firePropertyChange("projekt", this.projekt, projekt);
		this.projekt = projekt;
	}

	public String getDesyauftragsNr() {
		return desyauftragsNr;
	}

	public void setDesyauftragsNr(String desyauftragsNr) {
		pcs.firePropertyChange("desyauftragsNr", this.desyauftragsNr, desyauftragsNr);
		this.desyauftragsNr = desyauftragsNr;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		pcs.firePropertyChange("beschreibung", this.beschreibung, beschreibung);
		this.beschreibung = beschreibung;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		pcs.firePropertyChange("text", this.text, text);
		this.text = text;
	}

	public String getBaType() {
		return baType;
	}

	public void setBaType(String baType) {
		this.baType = baType;
	}

	public BigDecimal getVorherigeBa() {
		return vorherigeBa;
	}

	public void setVorherigeBba(BigDecimal vorherigeBa) {
		pcs.firePropertyChange("vorherigeBa", this.vorherigeBa, vorherigeBa);
		this.vorherigeBa = vorherigeBa;
	}

	public BigDecimal getGesamtwert() {
		return gesamtwert;
	}

	public void setGesamtwert(BigDecimal gesamtwert) {
		pcs.firePropertyChange("gesamtwert", this.gesamtwert, gesamtwert);
		this.gesamtwert = gesamtwert;
	}

	public Date getTermin() {
		return termin;
	}

	public void setTermin(Date termin) {
		pcs.firePropertyChange("termin", this.termin, termin);
		this.termin = termin;
	}

	public Boolean getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(Boolean maintenanceContract) {
		pcs.firePropertyChange("maintenanceContract", this.maintenanceContract, maintenanceContract);
		this.maintenanceContract = maintenanceContract;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		pcs.firePropertyChange("validUntil", this.validUntil, validUntil);
		this.validUntil = validUntil;
	}

	public Boolean getRememberExpiration() {
		return rememberExpiration;
	}

	public void setRememberExpiration(Boolean rememberExpiration) {
		pcs.firePropertyChange("rememberExpiration", this.rememberExpiration, rememberExpiration);
		this.rememberExpiration = rememberExpiration;
	}

	public String getKostenstelle() {
		return kostenstelle;
	}

	public void setKostenstelle(String kostenstelle) {
		pcs.firePropertyChange("kostenstelle", this.kostenstelle, kostenstelle);
		this.kostenstelle = kostenstelle;
	}

	public BigDecimal getZuInventarNr() {
		return zuInventarNr;
	}

	public void setZuInventarNr(BigDecimal zuInventarNr) {
		this.zuInventarNr = zuInventarNr;
	}

	public Date getAustellungsDatum() {
		return austellungsDatum;
	}

	public void setAustellungsDatum(Date austellungsDatum) {
		this.austellungsDatum = austellungsDatum;
	}

	public void setVorherigeBa(BigDecimal vorherigeBa) {
		this.vorherigeBa = vorherigeBa;
	}

	@Override
	public String getValue() {
	   return nummer.toString();
	}

	public String toString() {
	  if (nummer == null) {
	      return null;
	   }
		return nummer.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(nummer).toHashCode();
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

		Order rhs = (Order) obj;

		return new EqualsBuilder().append(nummer, rhs.nummer).isEquals();
	}

	@Override
	public Order deepClone() {
		try {
			Order clone = new Order();
			clone.nummer = null;
			clone.firmaName = firmaName;
			clone.aussteller = aussteller;
			clone.gruppe = gruppe;
			clone.abladeStelle = abladeStelle;
			clone.projekt = projekt;
			clone.desyauftragsNr = desyauftragsNr;
			clone.beschreibung = beschreibung;
			clone.text = text;
			clone.baType = baType;
			clone.kostenstelle = kostenstelle;
			clone.vorherigeBa =vorherigeBa ;
			clone.zuInventarNr = zuInventarNr;
			clone.gesamtwert = gesamtwert;
			clone.termin = termin;
			clone.austellungsDatum = austellungsDatum;
			clone.maintenanceContract = maintenanceContract;
			clone.validUntil = validUntil;
			clone.rememberExpiration = rememberExpiration;			
			clone.orderPositions = new ArrayList<OrderPos>();
			for (OrderPos orderPos : this.orderPositions) {
				OrderPos clonePos = orderPos.deepClone();
				clonePos.setOrder(clone);
				clone.orderPositions.add(clonePos);
			}
			return clone;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
