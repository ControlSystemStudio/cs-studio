/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.utility.dbparser.data;

import java.util.List;

public class Template {
	
	private List<Record> epicsRecords;

	public List<Record> getEPICSRecords() {
		return epicsRecords;
	}

	public void setEPICSRecords(List<Record> epicsRecords) {
		this.epicsRecords = epicsRecords;
	}
}
