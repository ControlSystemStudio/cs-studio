package org.csstudio.platform.statistic;

import java.util.GregorianCalendar;

public class StoredData {
	
	private GregorianCalendar	time = null;
	private Double				value = null;
	private Double				count = 0.0;
	
	public StoredData ( Double value) {
		/*
		 * initialization
		 */
		setTime( new GregorianCalendar());
		setValue ( value);
	}
	
	public Double getCount() {
		return count;
	}
	public void setCount(Double count) {
		this.count = count;
	}
	public GregorianCalendar getTime() {
		return time;
	}
	public void setTime(GregorianCalendar time) {
		this.time = time;
	}
	public void setActualTime() {
		this.time = new GregorianCalendar();
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

}
