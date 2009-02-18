package com.cosylab.vdct.vdb;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.*;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.LinkSource;

/**
 * Handles PV_LINK properties
 * Creation date: (1.2.2001 22:24:28)
 * @author Matej Sekoranja
 */
 
public final class LinkProperties {
	private static final String tokenizerSettings	= " .\t";
	
	private static final String nullString 		= "";
	private static final String spaceString 	= " ";
	
	private static final String defaultVarName	= "VAL";		// defaults
	private static final String defaultProcess	= "NPP";			
	private static final String defaultMaximize	= "NMS";

	public final static int NOT_VALID = -1;
	public final static int INLINK_FIELD = 0;
	public final static int OUTLINK_FIELD = 1;
	public final static int FWDLINK_FIELD = 2;
	public final static int VARIABLE_FIELD = 3;
	public final static int PORT_FIELD = 4;
	public final static int TEMPLATE_MACRO = 5;

	private int type;
	private String varName;
	private String process;
	private String maximize;
	private String record;
	private boolean isInterGroupLink;
	
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:53:33)
 */
public LinkProperties(LinkSource fd) {
	setDefaults();
	setProperties(fd);
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @return java.lang.String
 */
public java.lang.String getMaximize() {
	return maximize;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:01:58)
 * @return java.lang.String
 */
public String getOptions() {
	return process+spaceString+maximize;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:01:58)
 * @return java.lang.String
 */
public String getCompactOptions() {
	if (maximize.equals(defaultMaximize) && process.equals(defaultProcess))
		return nullString;
	else if (maximize.equals(defaultMaximize))
		return process;
	else
		return process+spaceString+maximize;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:01:58)
 * @return java.lang.String
 */
public String getCompactLinkDef() {
	String link = record;
	/// !!!! proc
	if (!varName.equals(defaultVarName))
		link += Constants.FIELD_SEPARATOR + varName;
	String opt = getCompactOptions();
	if (!opt.equals(nullString))
		link += spaceString + opt;
	return link;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:01:58)
 * @return java.lang.String
 */
public String getTarget() {
	String link = record;
	/// !!!! proc
	link += Constants.FIELD_SEPARATOR + varName;
	return link;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param fd com.cosylab.vdct.vdb.VDBFieldData
 */
public static String getOptions(LinkSource fd) {

	String value = fd.getValue();
	if (value == null || value.length() == 0 || //value.equals(nullString) ||
		// check all tokenizer separators 
		value.charAt(0) == ' ' || value.charAt(0) == '.' || value.charAt(0) == '\t')
		return null;

	StringTokenizer tokenizer = new StringTokenizer(value, tokenizerSettings);

	String process = defaultProcess;
	String maximize = defaultMaximize;
	
	if (tokenizer.hasMoreTokens()) tokenizer.nextToken();		// read record name 
	if (value.indexOf(Constants.FIELD_SEPARATOR) > -1) 
		if (tokenizer.hasMoreTokens()) tokenizer.nextToken(); // read var variable

	if (tokenizer.hasMoreTokens()) process=tokenizer.nextToken(); // read var variable process
	if (tokenizer.hasMoreTokens()) maximize=tokenizer.nextToken(); // read var variable maxmimize
	else {
		// checks if process variable is actually maximize variable?!
		if (process.equals("NMS") || 
			process.equals("MS")) {
			maximize=process;
			process=defaultProcess;
		}
	}

	return process+spaceString+maximize;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @return java.lang.String
 */
public java.lang.String getProcess() {
	return process;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @return java.lang.String
 */
public java.lang.String getRecord() {
	return record;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:43:15)
 * @return java.lang.String
 * @param fd com.cosylab.vdct.vdb.VDBFieldData
 */
public static String getTarget(LinkSource fd) {
	
	String target = nullString;
	String value = fd.getValue();

	if (value == null || value.length() == 0 || //value.equals(nullString) ||
		// check all tokenizer separators 
		value.charAt(0) == ' ' || value.charAt(0) == '.' || value.charAt(0) == '\t')
		return null;

	StringTokenizer tokenizer = new StringTokenizer(value, tokenizerSettings);

	if (tokenizer.hasMoreTokens()) target=tokenizer.nextToken();		// read record name 
	if (value.indexOf(Constants.FIELD_SEPARATOR) > -1) {
		if (tokenizer.hasMoreTokens()) {
			String var = tokenizer.nextToken(); 				// read var variable
			target+=Constants.FIELD_SEPARATOR+var;
		}
	}

	return target;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:43:15)
 * @return java.lang.String
 * @param value java.lang.String
 */
public static String getTargetFromString(String value) {
	
	if (value == null || value.length() == 0 || //value.equals(nullString) ||
		// check all tokenizer separators 
		value.charAt(0) == ' ' || value.charAt(0) == '.' || value.charAt(0) == '\t')
		return null;

	String target = nullString;


	StringTokenizer tokenizer = new StringTokenizer(value, tokenizerSettings);

	if (tokenizer.hasMoreTokens()) target=tokenizer.nextToken();		// read record name 
	if (value.indexOf(Constants.FIELD_SEPARATOR) > -1) {
		if (tokenizer.hasMoreTokens()) {
			String var = tokenizer.nextToken(); 				// read var variable
				target+=Constants.FIELD_SEPARATOR+var;
		}
	}

	return target;
}

public static String getRecordFromString(String value) {
	
	if (value == null || value.length() == 0 || //value.equals(nullString) ||
		// check all tokenizer separators 
		value.charAt(0) == ' ' || value.charAt(0) == '.' || value.charAt(0) == '\t')
		return null;

	String record = nullString;


	StringTokenizer tokenizer = new StringTokenizer(value, tokenizerSettings);

	if (tokenizer.hasMoreTokens()) record=tokenizer.nextToken();		// read record name 
	
	return record;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:55:09)
 * @return int
 * @param fd com.cosylab.vdct.graphics.object.LinkSource
 */
public static int getType(LinkSource fd) {
	switch (fd.getType()) {
		case DBDConstants.DBF_INLINK  : return INLINK_FIELD;
		case DBDConstants.DBF_OUTLINK : return OUTLINK_FIELD;
		case DBDConstants.DBF_FWDLINK : return FWDLINK_FIELD;
		case DBDConstants.DBF_PORT    : return PORT_FIELD;
		case DBDConstants.DBF_TEMPLATE_MACRO : return TEMPLATE_MACRO;
		default: return VARIABLE_FIELD;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @return java.lang.String
 */
public java.lang.String getVarName() {
	return varName;
}
/**
 * Insert the method's description here.
 * Creation date: (31.1.2001 20:36:39)
 * @return boolean
 */
public boolean isIsInterGroupLink() {
	return isInterGroupLink;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 10:02:52)
 */
private void setDefaults() {
	setType(NOT_VALID);
	setRecord(nullString);
	setProcess(defaultProcess);
	setMaximize(defaultMaximize);
	setVarName(defaultVarName);
	setIsInterGroupLink(false);
}
/**
 * Insert the method's description here.
 * Creation date: (31.1.2001 20:36:39)
 * @param newIsInterGroupLink boolean
 */
public void setIsInterGroupLink(boolean newIsInterGroupLink) {
	isInterGroupLink = newIsInterGroupLink;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @param newMaximize java.lang.String
 */
public void setMaximize(java.lang.String newMaximize) {
	maximize = newMaximize;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @param newProcess java.lang.String
 */
public void setProcess(java.lang.String newProcess) {
	process = newProcess;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:54:05)
 * @param fd com.cosylab.vdct.graphics.object.LinkSource
 */
private void setProperties(LinkSource fd) {

	String value = fd.getValue();

	if (value == null || value.length() == 0 || //value.equals(nullString) ||
		// check all tokenizer separators 
		value.charAt(0) == ' ' || value.charAt(0) == '.' || value.charAt(0) == '\t')
	{
		setType(NOT_VALID);
		setRecord(null);
		return;
	}
	
	setType(getType(fd));

	StringTokenizer tokenizer = new StringTokenizer(value, tokenizerSettings);

	if (tokenizer.hasMoreTokens()) setRecord(tokenizer.nextToken());	// read record name 
	if (value.indexOf(Constants.FIELD_SEPARATOR) > -1) {
		if (tokenizer.hasMoreTokens()) {
			setVarName(tokenizer.nextToken()); // read var variable
		}
	}

	if (tokenizer.hasMoreTokens()) setProcess(tokenizer.nextToken()); // read var variable process
	if (tokenizer.hasMoreTokens()) setMaximize(tokenizer.nextToken()); // read var variable maxmimize
	else {
		// checks if process variable is actually maximize variable?!
		if (getProcess().equals("NMS") || 
			getProcess().equals("MS")) {
			setMaximize(getProcess());
			setProcess(defaultProcess);
		}
	}
	if (getType() == FWDLINK_FIELD) {
        if (getProcess().equals("NPP"))
            setProcess("PP");
    } else if (getType() == TEMPLATE_MACRO) {
        setProcess("");
        setMaximize("");
    }

//	if (Group.substractParentName(fd.getRecord().getName()).equals(Group.substractParentName(getRecord())))
	if (Group.substractParentName(fd.getFullName()).equals(Group.substractParentName(getRecord())))
		setIsInterGroupLink(false);
	else
		setIsInterGroupLink(true);
	
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @param newRecord java.lang.String
 */
public void setRecord(java.lang.String newRecord) {
	record = newRecord;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @param newType int
 */
public void setType(int newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:56:35)
 * @param newVarName java.lang.String
 */
public void setVarName(java.lang.String newVarName) {
	varName = newVarName;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:38:32)
 */
public void update(LinkSource fd) {
	setProperties(fd);
}
}
