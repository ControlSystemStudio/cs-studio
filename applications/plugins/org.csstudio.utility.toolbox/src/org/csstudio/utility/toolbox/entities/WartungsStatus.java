package org.csstudio.utility.toolbox.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Table(name = "WARTUNG_STATUS")
@NamedQueries({ @NamedQuery(name = WartungsStatus.FIND_ALL, query = "from WartungsStatus s order by s.status") })
@Entity
public class WartungsStatus implements TextValue {

	public static final String FIND_ALL = "WartungsStatus.findAll";

	@Id
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
