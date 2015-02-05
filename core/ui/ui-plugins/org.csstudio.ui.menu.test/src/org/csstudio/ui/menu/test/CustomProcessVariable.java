package org.csstudio.ui.menu.test;

public class CustomProcessVariable {
	private final String name;
	public CustomProcessVariable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "CustomProcessVariable '" + name + "'";
	}
}
