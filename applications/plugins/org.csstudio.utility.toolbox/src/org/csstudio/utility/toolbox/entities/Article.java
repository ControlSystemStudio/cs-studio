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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.annotations.InputLength;
import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.hibernate.annotations.Immutable;

@Table(name = "artikel_daten")
@NamedQueries({
			@NamedQuery(name = Article.FIND_IN_GROUP, query = "from Article a where a.gruppeArtikel = :gruppeArtikel order by a.id"),
			@NamedQuery(name = Article.FIND_WITH_INTERN_ID, query = "from Article a where a.internId = :internId"),
			@NamedQuery(name = Article.FIND_BY_ID, query = "from Article a where a.id = :id"),
			@NamedQuery(name = Article.FIND_ALL_INSTALLED, query = "from ArticleInstalled a where a.artikelDatenId = :artikelDatenId order by id") })
@Entity
public class Article extends BindingEntity implements TextValue, Cloneable<Article> {

	private static final long serialVersionUID = -1L;

	public static final String FIND_IN_GROUP = "Article.findGroup";
	public static final String FIND_WITH_INTERN_ID = "Article.findWithInternId";
	public static final String FIND_BY_ID = "Article.findById";
	public static final String FIND_ALL_INSTALLED = "Article.findAllInstalled";

	@Id
	private BigDecimal id;

	@Column(name = "intern_id", unique = true)
	@Size(max = 20)
	private String internId;

	@Column(name = "serien_nr")
	@Size(max = 30)
	private String serienNr;

	@Column(name = "gruppe_artikel")
	@ReadOnly
	private BigDecimal gruppeArtikel;

	@Column(name = "inventar_nr")
	@InputLength(10)
	private BigDecimal inventarNr;

	@Column(name = "gruppe")
	@Size(max = 10)
	private String gruppe;

	@Column(name = "status")
	@Size(max = 20)
	private String status;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "artikel_beschreibung_id", referencedColumnName = "id")
	@Valid
	private ArticleDescription articleDescription;

	@Transient
	private int index;

	@PrePersist
	@PreUpdate
	protected void preUpdateRecordd() {
		if (gruppeArtikel == null) {
			gruppeArtikel = id;
		}
	}

	@PostPersist
	@PostUpdate
	protected void recordChanged() {
		pcs.firePropertyChange("id", null, id);
	}

	@PostLoad
	protected void recordLoaded() {
		setNewRecord(false);
	}

	public ArticleDescription getArticleDescription() {
		return articleDescription;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getId() {
		return id;
	}

	@Transient
	@Immutable
	public String getBeschreibung() {
		if (this.articleDescription == null) {
			return "";
		}
		return StringUtils.stripToEmpty(this.articleDescription.getBeschreibung());
	}

	public String getGruppe() {
		return gruppe;
	}

	public BigDecimal getGruppeArtikel() {
		return gruppeArtikel;
	}

	public int getIndex() {
		return index;
	}

	public String getInternId() {
		return internId;
	}

	public BigDecimal getInventarNr() {
		return inventarNr;
	}

	@Override
	public String getValue() {
		return String.valueOf(index);
	}

	@Transient
	@Immutable
	public String getLieferantName() {
		if (this.articleDescription == null) {
			return "";
		}
		return StringUtils.stripToEmpty(this.articleDescription.getLieferantName());
	}

	public String getSerienNr() {
		return serienNr;
	}

	public String getStatus() {
		return status;
	}

	public void setArticleDescription(ArticleDescription articleDescription) {
		pcs.firePropertyChange("articleDescription", this.articleDescription, articleDescription);
		this.articleDescription = articleDescription;
	}

	public void setGruppe(String gruppe) {
		pcs.firePropertyChange("gruppe", this.gruppe, gruppe);
		this.gruppe = gruppe;
	}

	public void setGruppeArtikel(BigDecimal gruppeArtikel) {
		pcs.firePropertyChange("gruppeArtikel", this.gruppeArtikel, gruppeArtikel);
		this.gruppeArtikel = gruppeArtikel;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setInternId(String internId) {
		pcs.firePropertyChange("internId", this.internId, internId);
		this.internId = internId;
	}

	public void setInventarNr(BigDecimal inventarNr) {
		pcs.firePropertyChange("inventarNr", this.inventarNr, inventarNr);
		this.inventarNr = inventarNr;
	}

	public void setSerienNr(String serienNr) {
		pcs.firePropertyChange("serienNr", this.serienNr, serienNr);
		this.serienNr = serienNr;
	}

	public void setStatus(String status) {
		pcs.firePropertyChange("status", this.status, status);
		this.status = status;
	}

	public Article createPrototype(BigDecimal gruppeArtikel) {
		Article article = new Article();
		article.articleDescription = this.articleDescription;
		article.gruppeArtikel = gruppeArtikel;
		article.status = "nicht definiert";
		return article;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (id == null) {
			return new HashCodeBuilder(17, 37).append(index).toHashCode();
		} else {
			return new HashCodeBuilder(17, 37).append(id).toHashCode();
		}
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

		Article rhs = (Article) obj;

		if (id == null) {
			return new EqualsBuilder().append(index, rhs.index).isEquals();
		} else {
			return new EqualsBuilder().append(id, rhs.id).isEquals();
		}
	}
	
	@Override
	public Article deepClone() {
		try {
			Article clone = new Article();
			clone.internId = null;
			clone.serienNr = this.serienNr;
			clone.gruppeArtikel = this.gruppeArtikel;
			clone.inventarNr = this.inventarNr;
			clone.gruppe = this.gruppe;
			clone.status = this.status;
			clone.articleDescription = getArticleDescription();
			clone.index = index;			
			clone.id = null;
			return clone;
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

}
