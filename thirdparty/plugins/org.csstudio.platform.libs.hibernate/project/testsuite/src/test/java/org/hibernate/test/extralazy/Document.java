//$Id: Document.java 7635 2005-07-24 23:04:30Z oneovthafew $
package org.hibernate.test.extralazy;

public class Document {

	private String title;
	private String content;
	private User owner;
	
	Document() {}
	
	public Document(String title, String content, User owner) {
		this.content = content;
		this.owner = owner;
		this.title = title;
		owner.getDocuments().add(this);
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
