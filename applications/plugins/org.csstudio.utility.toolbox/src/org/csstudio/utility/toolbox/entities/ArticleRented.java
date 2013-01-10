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

@Table(name = "artikel_ausgeliehen")
@NamedQueries({
	@NamedQuery(name = ArticleRented.FIND_RECORD, query = "from ArticleRented a where a.artikelDatenId = :artikelDatenId order by id desc")}
)
@Entity
public class ArticleRented  extends BindingEntity implements ArticleHistoryInfo {

	private static final long serialVersionUID = -1;
	
	public static final String FIND_RECORD = "ArticleRented.findRecord";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GBP")
	@SequenceGenerator(name = "GBP", sequenceName = "SEQ_ARTIKEL_AUSGELIEHEN")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	@ReadOnly
	private BigDecimal id;

	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;
	
	@Column(name = "name")
	@Size(max = 30)
	private String name;
	
	@Column(name = "group_name")
	@Size(max = 10)
	private String groupName;
	
	@Column(name = "date_back")
	private Date dateBack;
	
	@Column(name = "ausgeliehen_am")
	@NotNull
	private Date rentDate;

	@Column(name = "address")
	@Size(max = 128)
	private String address;

	@Column(name = "ausgeliehen_durch")
	@Size(max = 30)
	@NotEmpty
	private String rentedBy;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		pcs.firePropertyChange("address", this.address, address);
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		pcs.firePropertyChange("name", this.name, name);
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		pcs.firePropertyChange("groupName", this.groupName, groupName);
		this.groupName = groupName;
	}

	public Date getDateBack() {
		return dateBack;
	}

	public void setDateBack(Date dateBack) {
		pcs.firePropertyChange("dateBack", this.dateBack, dateBack);
		this.dateBack = dateBack;
	}

	public Date getRentDate() {
		return rentDate;
	}

	public void setRentDate(Date rentDate) {
		pcs.firePropertyChange("rentDate", this.rentDate, rentDate);
		this.rentDate = rentDate;
	}

	public String getRentedBy() {
		return rentedBy;
	}

	public void setRentedBy(String rentedBy) {
		pcs.firePropertyChange("rentedBy", this.rentedBy, rentedBy);
		this.rentedBy = rentedBy;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public Date getDate() {
		return rentDate;
	}

	@Override
	public String getStatusDescritpion() {
		return "Ausgeliehen";
	}

}
