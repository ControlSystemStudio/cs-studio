package org.csstudio.askap.navigator.model;

public class Node {
	private String name;
	private String opiName;
	private Branch branches[];
	
	private String macros[][];
	
	public Node(String name, String opiName, Branch branches[], String macros[][]) {
		this.name = name;
		this.opiName = opiName;
		this.branches = branches;
		this.macros = macros;
	}
	
	public Node() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpiName() {
		return opiName;
	}

	public void setOpiName(String opiName) {
		this.opiName = opiName;
	}

	public Branch[] getBranches() {
		return branches;
	}

	public void setBranches(Branch[] branches) {
		this.branches = branches;
	}

	public String[][] getMacros() {
		return macros;
	}

	public void setMacros(String[][] macros) {
		this.macros = macros;
	}

	public void setupMacros(View parentView) {
		if (branches==null)
			return;
		
		for (Branch branch : branches) {
			branch.setupMacros(macros, parentView);
		}
	}	
}
