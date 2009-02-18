package com.cosylab.vdct.db;

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

import com.cosylab.vdct.Console;
import com.cosylab.vdct.dbd.*;
import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class DBData {
	
	// also this file is a template
	protected DBTemplate templateData = null;


	protected Hashtable lines = null;
	protected Hashtable boxes = null;
	protected Hashtable textboxes = null;
	protected Hashtable records = null;
	protected Vector recordsV = null;			// ordered
	protected Hashtable groups = null;
	protected Hashtable links = null;
	protected Hashtable connectors = null;
	protected Hashtable templates = null;		// templates loaded by the way
	protected Vector templatesV = null;		// templates loaded by the way
	protected Hashtable templateInstances = null;
	
	protected DBView view = null;
	
	// contains DB structure (entry (include, path, addpath statements), record, expand)
	protected Vector structure = null; 
	
/**
 * DBDData constructor comment.
 */
public DBData(String id, String fileName) {
	
	templateData = new DBTemplate(id, fileName);

	structure = new Vector();
	
	records = new Hashtable();
	recordsV = new Vector();
	groups = new Hashtable();
	links = new Hashtable();
	connectors = new Hashtable();
	templates = new Hashtable();
	templatesV = new Vector();
	templateInstances = new Hashtable();
	lines = new Hashtable();
	boxes = new Hashtable();
	textboxes = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:16:22)
 * @param connector com.cosylab.vdct.db.DBConnectorData
 */
public void addConnector(DBConnectorData connector) {
	if (!connectors.containsKey(connector.getConnectorID()))
		connectors.put(connector.getConnectorID(), connector);
}
/**
 * This method was created in VisualAge.
 * @param gd com.cosylab.vdct.db.DBGroupData
 */
public void addGroup(DBGroupData gd) {
	if (!groups.containsKey(gd.getName()))
		groups.put(gd.getName(), gd);
}
/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBLinkData
 */
public void addLink(DBLinkData ld) {
	if (!links.containsKey(ld.getFieldName()))
		links.put(ld.getFieldName(), ld);
}

/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBLine
 */
public void addLine(DBLine ld) {
	if (!lines.containsKey(ld.getName()))
		lines.put(ld.getName(), ld);
}

/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBEntry
 */
public void addEntry(DBEntry ed) {
	if (!structure.contains(ed))
		structure.addElement(ed);
}

/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBBox
 */
public void addBox(DBBox bd) {
	if (!boxes.containsKey(bd.getName()))
		boxes.put(bd.getName(), bd);
}

/**
 * This method was created in VisualAge.
 * @param ld com.cosylab.vdct.db.DBTextBox
 */
public void addTextBox(DBTextBox td) {
	if (!textboxes.containsKey(td.getName()))
		textboxes.put(td.getName(), td);
}

/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.RecordData
 */
public void addRecord(DBRecordData rd) {
	if (!records.containsKey(rd.getName())) {
		records.put(rd.getName(), rd);
		recordsV.addElement(rd);
		structure.addElement(rd);		// add entry
	}
	else
		Console.getInstance().println("Warning: Record with name '"+rd.getName()+"' already exists, skiping...");
}
/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBTemplateInstance
 */
public void addTemplateInstance(DBTemplateInstance ti) {
	if (!templateInstances.containsKey(ti.getTemplateInstanceId())) {
		templateInstances.put(ti.getTemplateInstanceId(), ti);
		structure.addElement(ti);		// add entry
	}
	else
		Console.getInstance().println("Warning: Template instance of '"+ti.getTemplateInstanceId()+"' already exists, skiping...");
}

/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.db.DBTemplate
 */
public void addTemplate(DBTemplate t) {
	if (!templates.containsKey(t.getId())) {
		templates.put(t.getId(), t);
		templatesV.addElement(t);
	}
	else
		Console.getInstance().println("Warning: Template of '"+t.getId()+"' already exists, skiping...");
}

/**
 * Check is DTYP field is defined before INP and OUT fields...
 * Insert the method's description here.
 * Creation date: (18.11.1999 18:26:27)
 */

public static void checkDTYPfield(DBData db, DBDData dbd) {

 DBDRecordData dbdRecord;

 DBRecordData dbRecord;
 Enumeration e = db.getRecordsV().elements();
 Enumeration e2;
 while (e.hasMoreElements()) {
	dbRecord = (DBRecordData)e.nextElement();
	dbdRecord = dbd.getDBDRecordData(dbRecord.getRecord_type());
	if (dbdRecord!=null) {
		
		DBFieldData dbField2 = null;
		DBFieldData dbField = (DBFieldData)dbRecord.getFields().get("DTYP");
		if (dbField!=null) {
			e2 = dbRecord.getFieldsV().elements();
			dbField2 = (DBFieldData)e2.nextElement();
			while (dbField!=dbField2) {
				if (dbField2.getName().equals("INP") || dbField2.getName().equals("OUT")) 
					break;
				else
					dbField2 = (DBFieldData)e2.nextElement();
			}

			if (dbField!=dbField2) 
				Console.getInstance().println("Warning: "+dbRecord.name+" -> DTYP field needs to be defined before two fields INP and OUT...");
		}

	}
 }			 

}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:15:41)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getConnectors() {
	return connectors;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getGroups() {
	return groups;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getLinks() {
	return links;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:07:57)
 * @return java.util.Hashtable
 */
public Hashtable getRecords() {
	return records;
}
/**
 * Returns ordered (as read) list of records
 * Creation date: (6.1.2001 20:37:16)
 * @return java.util.Vector
 */
public Vector getRecordsV() {
	return recordsV;
}
/**
 * Returns the templateInstances.
 * @return Hashtable
 */
public Hashtable getTemplateInstances()
{
	return templateInstances;
}

/**
 * Returns the templates.
 * @return Hashtable
 */
public Hashtable getTemplates()
{
	return templates;
}

/**
 * Returns the templates.
 * @return Hashtable
 */
public Vector getTemplatesV()
{
	return templatesV;
}

	/**
	 * Returns the boxes.
	 * @return Hashtable
	 */
	public Hashtable getBoxes()
	{
		return boxes;
	}

	/**
	 * Returns the lines.
	 * @return Hashtable
	 */
	public Hashtable getLines()
	{
		return lines;
	}

	/**
	 * Returns the textboxes.
	 * @return Hashtable
	 */
	public Hashtable getTextboxes()
	{
		return textboxes;
	}

	/**
	 * Returns the templateData.
	 * @return DBTemplate
	 */
	public DBTemplate getTemplateData()
	{
		return templateData;
	}

	/**
	 * Returns the structure.
	 * @return Vector
	 */
	public Vector getStructure()
	{
		return structure;
	}

	/**
	 * @return
	 */
	public DBView getView() {
		return view;
	}

	/**
	 * @param view
	 */
	public void setView(DBView view) {
		this.view = view;
	}

}
