package org.csstudio.alarm.treeView.views.models;

import java.util.Date;
import java.util.Hashtable;

public class Alarm implements Comparable{

	protected int severity=0;
	protected boolean unAcknowledged = false;
	protected String source;
	protected Date timestamp;
	protected String name;
	protected Hashtable<String,String> properties;

	public Alarm(int severity, String source){
		this.severity = severity;
		this.source = source;
	}	
	
	public Alarm(){
		severity = 0;
		source = "";
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Hashtable<String, String> getProperties() {
		return properties;
	}


	public void setProperties(Hashtable<String, String> properties) {
		this.properties = properties;
	}


	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	public void disable(){
		severity=0;
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if (arg0 instanceof Alarm){
			return ((Alarm)arg0).getSeverity()-this.severity;
		}
		return 0;
	}
	
	public String getName(){
		return name;
	}
	
	public void setUnAcknowledged(boolean unAcknowledged) {
		this.unAcknowledged = unAcknowledged;
	}

	public String toString(){
		return "Alarm";
	}

	public boolean isUnAcknowledged() {
		return unAcknowledged;
	}
}
