package org.csstudio.askap.navigator.model;

public class OPI {

	private String name = "";
	private String opiFile = "";
	
	public OPI(String name, String opi) {
		this.name = name;
		this.opiFile = opi;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpiFile() {
		return opiFile;
	}
	public void setOpiFile(String opiFile) {
		this.opiFile = opiFile;
	}
}
