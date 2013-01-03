package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "artikel_in_wartung")
@NamedQueries({ @NamedQuery(name = ArticleMaintenance.FIND_RECORD, query = "from ArticleMaintenance a where a.artikelDatenId = :artikelDatenId order by id desc") })
@Entity
public class ArticleMaintenance extends BindingEntity implements ArticleHistoryInfo {

	private static final long serialVersionUID = 1L;

	public static final String FIND_RECORD = "ArticleMaintenance.findRecord";

	@Id
	@ReadOnly
	private String id;

	@Column(name = "artikel_daten_id")
	@NotNull
	private BigDecimal artikelDatenId;

	@Column(name = "bei_firma")
	@Size(max = 30)
	private String beiFirma;

	@Column(name = "bei_gruppe")
	@Size(max = 10)
	private String beiGruppe;

	@Column(name = "bei_account")
	@Size(max = 30)
	private String beiAccount;

	@Column(name = "wartung_durch")
	@Size(max = 30)
	private String wartungDurch;

	@Column(name = "project")
	@Size(max = 30)
	private String project;

	@Column(name = "device")
	@Size(max = 30)
	private String device;

	@Column(name = "keywords")
	@Size(max = 30)
	private String keywords;

	@Column(name = "location")
	@Size(max = 30)
	private String location;

	@Column(name = "descshort")
	@Size(max = 60)
	@NotEmpty
	private String descShort;

	@Column(name = "desclong")
	@Size(max = 400)
	@NotEmpty
	private String descLong;

	@Column(name = "sendemailto")
	@Size(max = 200)
	private String sendEmailTo;

	@Column(name = "start_request")
	private Date startRequest;

	@Column(name = "finish_request")
	private Date finishRequest;

	@Column(name = "status")
	private String status;

	@Column(name = "status_vom")
	private Date statusVom;

	@Transient
	private String repair;

	@Transient
	private boolean software;

	@Transient
	private boolean hardware;

	@Transient
	private boolean emailGroup;

	@Transient
	private boolean emailAccount;

	@Transient
	private boolean repairGroup;

	@Transient
	private boolean repairAccount;

	@Transient
	private boolean repairCompany;

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		pcs.firePropertyChange("id", this.id, id);
		this.id = id;
	}

	public BigDecimal getArtikelDatenId() {
		return artikelDatenId;
	}

	public void setArtikelDatenId(BigDecimal artikelDatenId) {
		this.artikelDatenId = artikelDatenId;
	}

	public String getBeiFirma() {
		return beiFirma;
	}

	public void setBeiFirma(String beiFirma) {
		pcs.firePropertyChange("beiFirma", this.beiFirma, beiFirma);
		this.beiFirma = beiFirma;
	}

	public String getBeiGruppe() {
		return beiGruppe;
	}

	public void setBeiGruppe(String beiGruppe) {
		pcs.firePropertyChange("beiGruppe", this.beiGruppe, beiGruppe);
		this.beiGruppe = beiGruppe;
	}

	public String getBeiAccount() {
		return beiAccount;
	}

	public void setBeiAccount(String beiAccount) {
		pcs.firePropertyChange("beiAccount", this.beiAccount, beiAccount);
		this.beiAccount = beiAccount;
	}

	public String getWartungDurch() {
		return wartungDurch;
	}

	public void setWartungDurch(String wartungDurch) {
		pcs.firePropertyChange("wartungDurch", this.wartungDurch, wartungDurch);
		this.wartungDurch = wartungDurch;
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		pcs.firePropertyChange("keywords", this.keywords, keywords);
		this.keywords = keywords;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		pcs.firePropertyChange("location", this.location, location);
		this.location = location;
	}

	public String getDescShort() {
		return descShort;
	}

	public void setDescShort(String descShort) {
		pcs.firePropertyChange("descShort", this.descShort, descShort);
		this.descShort = descShort;
	}

	public String getDescLong() {
		return descLong;
	}

	public String getSendEmailTo() {
		return sendEmailTo;
	}

	public void setSendEmailTo(String sendEmailTo) {
		pcs.firePropertyChange("sendEmailTo", this.sendEmailTo, sendEmailTo);
		this.sendEmailTo = sendEmailTo;
	}

	public String getRepair() {
		return repair;
	}

	public void setRepair(String repair) {
		pcs.firePropertyChange("repair", this.repair, repair);
		this.repair = repair;
	}

	public void setDescLong(String descLong) {
		this.descLong = descLong;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusVom() {
		return statusVom;
	}

	public void setStatusVom(Date statusVom) {
		pcs.firePropertyChange("statusVom", this.statusVom, statusVom);
		this.statusVom = statusVom;
	}

	public Date getStartRequest() {
		return startRequest;
	}

	public void setStartRequest(Date startRequest) {
		pcs.firePropertyChange("startRequest", this.startRequest, startRequest);
		this.startRequest = startRequest;
	}

	public Date getFinishRequest() {
		return finishRequest;
	}

	public void setFinishRequest(Date finishRequest) {
		pcs.firePropertyChange("finishRequest", this.finishRequest, finishRequest);
		this.finishRequest = finishRequest;
	}

	public boolean isSoftware() {
		return software;
	}

	public void setSoftware(boolean software) {
		pcs.firePropertyChange("software", this.software, software);
		this.software = software;
	}

	public boolean isHardware() {
		return hardware;
	}

	public void setHardware(boolean hardware) {
		pcs.firePropertyChange("hardware", this.hardware, hardware);
		this.hardware = hardware;
	}

	public boolean isEmailGroup() {
		return emailGroup;
	}

	public void setEmailGroup(boolean emailGroup) {
		pcs.firePropertyChange("emailGroup", this.emailGroup, emailGroup);
		this.emailGroup = emailGroup;
	}

	public boolean isEmailAccount() {
		return emailAccount;
	}

	public void setEmailAccount(boolean emailAccount) {
		pcs.firePropertyChange("emailAccount", this.emailAccount, emailAccount);
		this.emailAccount = emailAccount;
	}

	public boolean isRepairGroup() {
		return repairGroup;
	}

	public void setRepairGroup(boolean repairGroup) {
		pcs.firePropertyChange("repairGroup", this.repairGroup, repairGroup);
		this.repairGroup = repairGroup;
	}

	public boolean isRepairAccount() {
		return repairAccount;
	}

	public void setRepairAccount(boolean repairAccount) {
		pcs.firePropertyChange("repairAccount", this.repairAccount, repairAccount);
		this.repairAccount = repairAccount;
	}

	public boolean isRepairCompany() {
		return repairCompany;
	}

	public void setRepairCompany(boolean repairCompany) {
		pcs.firePropertyChange("repairCompany", this.repairCompany, repairCompany);
		this.repairCompany = repairCompany;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);

	}

	@Override
	public Date getDate() {
		return startRequest;
	}

	@Override
	public String getStatusDescritpion() {
		return "In Reparatur";
	}
}
