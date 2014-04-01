/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.data;

public class Device {

	private final String recordType;
	private final String linkType;
	private final String dsetName;
	private final String choiceString;

	public Device(String recordType, String linkType, String dsetName,
			String choiceString) {
		this.recordType = recordType;
		this.linkType = linkType;
		this.dsetName = dsetName;
		this.choiceString = choiceString;
	}

	public String getRecordType() {
		return recordType;
	}

	public String getLinkType() {
		return linkType;
	}

	public String getDsetName() {
		return dsetName;
	}

	public String getChoiceString() {
		return choiceString;
	}

	@Override
	public String toString() {
		return "Device [recordType=" + recordType + ", linkType=" + linkType
				+ ", dsetName=" + dsetName + ", choiceString=" + choiceString
				+ "]";
	}

}
