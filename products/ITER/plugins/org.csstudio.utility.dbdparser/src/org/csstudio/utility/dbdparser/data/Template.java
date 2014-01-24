/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;

import java.util.ArrayList;
import java.util.List;

public class Template {

	private final List<Path> paths;
	private final List<Include> includes;
	private final List<Menu> menus;
	private final List<RecordType> recordTypes;
	private final List<Device> devices;
	private final List<Driver> drivers;
	private final List<Registrar> registrars;
	private final List<Variable> variables;
	private final List<Function> functions;
	private final List<Breaktable> breaktables;

	public Template() {
		this.paths = new ArrayList<Path>();
		this.includes = new ArrayList<Include>();
		this.menus = new ArrayList<Menu>();
		this.recordTypes = new ArrayList<RecordType>();
		this.devices = new ArrayList<Device>();
		this.drivers = new ArrayList<Driver>();
		this.registrars = new ArrayList<Registrar>();
		this.variables = new ArrayList<Variable>();
		this.functions = new ArrayList<Function>();
		this.breaktables = new ArrayList<Breaktable>();
	}

	public List<Path> getPaths() {
		return paths;
	}

	public List<Include> getIncludes() {
		return includes;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public List<RecordType> getRecordTypes() {
		return recordTypes;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public List<Driver> getDrivers() {
		return drivers;
	}

	public List<Registrar> getRegistrars() {
		return registrars;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public List<Breaktable> getBreaktables() {
		return breaktables;
	}

	@Override
	public String toString() {
		return "Template [\npaths=" + paths + ", \nincludes=" + includes
				+ ", \nmenus=" + menus + ", \nrecordTypes=" + recordTypes
				+ ", \ndevices=" + devices + ", \ndrivers=" + drivers
				+ ", \nregistrars=" + registrars + ", \nvariables=" + variables
				+ ", \nfunctions=" + functions + ", \nbreaktables="
				+ breaktables + "]";
	}

}
