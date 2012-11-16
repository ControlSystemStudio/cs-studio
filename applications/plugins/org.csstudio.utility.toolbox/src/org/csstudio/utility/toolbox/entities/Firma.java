package org.csstudio.utility.toolbox.entities;

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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "FIRMA")
@NamedQueries({ @NamedQuery(name = Firma.FIND_ALL, query = "select f from Firma f order by f.name") })
@Entity
public class Firma extends BindingEntity implements TextValue, Cloneable<Firma> {

	private static final long serialVersionUID = -1L;

	public static final String FIND_ALL = "Firma.findAll";

	@Id
	@Column(name = "NAME")
	@NotEmpty
	@ReadOnly
	private String name;

	@Column(name = "NAME_LANG")
	@NotEmpty
	private String nameLang;

	@Column(name = "strasse")
	private String strasse;

	@Column(name = "postleitzahl")
	private String postleitzahl;

	@Column(name = "stadt")
	private String stadt;

	@Column(name = "land")
	private String land;

	@Column(name = "telefon")
	private String telefon;

	@Column(name = "fax")
	private String fax;

	@Column(name = "email")
	private String email;

	@Column(name = "beschreibung")
	private String beschreibung;

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	@PostPersist
	@PostUpdate
	protected void recordChanged() {
		pcs.firePropertyChange("nummer", null, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		pcs.firePropertyChange("name", this.name, name);
		this.name = name;
	}

	public String getNameLang() {
		return nameLang;
	}

	public void setNameLang(String nameLang) {
		pcs.firePropertyChange("nameLang", this.nameLang, nameLang);
		this.nameLang = nameLang;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		pcs.firePropertyChange("strasse", this.strasse, strasse);
		this.strasse = strasse;
	}

	public String getPostleitzahl() {
		return postleitzahl;
	}

	public void setPostleitzahl(String postleitzahl) {
		pcs.firePropertyChange("postleitzahl", this.postleitzahl, postleitzahl);
		this.postleitzahl = postleitzahl;
	}

	public String getStadt() {
		return stadt;
	}

	public void setStadt(String stadt) {
		pcs.firePropertyChange("stadt", this.stadt, stadt);
		this.stadt = stadt;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		pcs.firePropertyChange("land", this.land, land);
		this.land = land;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		pcs.firePropertyChange("telefon", this.telefon, telefon);
		this.telefon = telefon;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		pcs.firePropertyChange("fax", this.fax, fax);
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		pcs.firePropertyChange("email", this.email, email);
		this.email = email;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		pcs.firePropertyChange("beschreibung", this.beschreibung, beschreibung);
		this.beschreibung = beschreibung;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	@Transient
	public String getValue() {
		return name;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(name).toHashCode();
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

		Firma rhs = (Firma) obj;

		return new EqualsBuilder().append(name, rhs.name).isEquals();
	}

	@Override
	public Firma deepClone() {
		try {
			Firma firma = (Firma) BeanUtils.cloneBean(this);
			firma.name = "{" + firma.name + "}";
			return firma;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
