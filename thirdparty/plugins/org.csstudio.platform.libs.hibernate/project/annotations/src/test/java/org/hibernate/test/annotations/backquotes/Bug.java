//$Id: Bug.java 14747 2008-06-06 08:16:25Z hardy.ferentschik $
package org.hibernate.test.annotations.backquotes;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Index;

@Entity
public class Bug 
{
	@Id
	@Column(name="`bug_id`")
	private int id;
	
	@Column(name="`title`")
	@Index(name="`titleindex`")
	private String title;
	
	@ManyToMany
	@JoinTable(name="`bug_category`")
	private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
