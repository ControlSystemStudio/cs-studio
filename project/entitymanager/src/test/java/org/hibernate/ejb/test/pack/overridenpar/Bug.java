//$Id: Bug.java 18499 2010-01-10 15:16:34Z stliu $
package org.hibernate.ejb.test.pack.overridenpar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Bug {
	@Id
	@GeneratedValue
	private Long id;
	private String subject;
	@Column(name="`comment`")
	private String comment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
