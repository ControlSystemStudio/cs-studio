package org.csstudio.utility.toolbox.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Table(name = "artikel_status")
@NamedQueries({ @NamedQuery(name = ArticleState.FIND_ALL, query = "from ArticleState s order by s.status") })
@Entity
public class ArticleState implements TextValue {

	public static final String FIND_ALL = "ArticleState.findAll";

	@Id
	@ReadOnly
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getValue() {
		return status;
	}
	
}
