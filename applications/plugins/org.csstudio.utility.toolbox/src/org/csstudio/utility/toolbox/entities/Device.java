package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Table(name = "DEVICE")
@NamedQueries({ @NamedQuery(name = Device.FIND_ALL, query = "select d from Device d order by d.keyword"),
			@NamedQuery(name = Device.FIND_BY_NAME, query = "select d from Device d where d.keyword = :keyword") })
@Entity
public class Device implements TextValue {

	public static final String FIND_ALL = "Device.findAll";
	public static final String FIND_BY_NAME = "Device.findByName";

	@Id
	@Column(name = "keyword")
	private String keyword;

	@Override
	public String getValue() {
		return keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	};

}
