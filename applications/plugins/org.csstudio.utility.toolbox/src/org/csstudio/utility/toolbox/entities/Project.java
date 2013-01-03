package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Table(name = "PROJECT")
@NamedQueries(
			{ @NamedQuery(name = Project.FIND_ALL, query = "select p from Project p order by p.keyword") ,
			 @NamedQuery(name = Project.FIND_BY_NAME, query = "select p from Project p where p.keyword = :keyword") 			
			})
@Entity
public class Project implements TextValue {

	public static final String FIND_ALL = "Project.findAll";
	public static final String FIND_BY_NAME = "Project.findByName";

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
