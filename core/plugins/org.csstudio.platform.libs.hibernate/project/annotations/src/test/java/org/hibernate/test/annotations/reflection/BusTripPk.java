//$Id: BusTripPk.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.reflection;

/**
 * @author Emmanuel Bernard
 */
public class BusTripPk {
	private String busNumber;
	private String busDriver;

	public String getBusDriver() {
		return busDriver;
	}

	public void setBusDriver(String busDriver) {
		this.busDriver = busDriver;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}
}
