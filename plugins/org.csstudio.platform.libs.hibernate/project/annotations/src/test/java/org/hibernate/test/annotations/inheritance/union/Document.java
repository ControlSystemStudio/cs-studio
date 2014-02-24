//$Id: Document.java 16294 2009-04-10 20:07:37Z gbadner $
package org.hibernate.test.annotations.inheritance.union;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "DocumentUnion")
public class Document extends File {
	private int size;

	Document() {
	}

	Document(String name, int size) {
		super( name );
		this.size = size;
	}

	@Column(name="xsize")
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
