package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Table(name = "KEYWORDHARD")
@NamedQueries({
			@NamedQuery(name = KeywordHardware.FIND_ALL, query = "select k from KeywordHardware k order by k.keyword"),
			@NamedQuery(name = KeywordHardware.FIND_BY_KEYWORD, query = "select k from KeywordHardware k where k.keyword = :keyword") })
@Entity
public class KeywordHardware implements TextValue {

	public static final String FIND_ALL = "KeywordHardware.findAll";
	public static final String FIND_BY_KEYWORD = "KeywordHardware.findByKeyWord";

	@Column(name = "KEYWORD")
	@Id
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
	}

}
