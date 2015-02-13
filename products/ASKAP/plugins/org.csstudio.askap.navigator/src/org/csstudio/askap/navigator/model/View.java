package org.csstudio.askap.navigator.model;

public class View {

	private String name;
	private Branch branches[];
	private Node node;
	
	public View(String name, Branch branches[], Node node) {
		this.name = name;
		this.branches = branches;
		this.node = node;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Branch[] getBranches() {
		return branches;
	}

	public void setBranches(Branch[] branches) {
		this.branches = branches;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setupMacros() {
		node.setupMacros(this);
	}
}
