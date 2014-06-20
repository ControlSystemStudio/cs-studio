/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.dbfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.diag.pvfields.DataProvider;
import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVInfo;
import org.csstudio.utility.dbparser.DBContext;
import org.csstudio.utility.dbparser.data.Field;
import org.csstudio.utility.dbparser.data.Record;

/**
 * PV Fields Data provider from DB files.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DBFilesDataProvider implements DataProvider {

	@Override
	public PVInfo lookup(final String name) throws Exception {
		final PVInfo info = searchInDB(name);
		if (info != null)
			return info;

		final Map<String, String> properties = new HashMap<String, String>();
		final List<PVField> fields = new ArrayList<PVField>();
		final PVInfo emptyInfo = new PVInfo(properties, fields);
		Logger.getLogger(getClass().getName()).log(Level.FINE,
				"DB Files Info for {0}: {1}", new Object[] { name, emptyInfo });
		return emptyInfo;
	}
	
	private PVInfo searchInDB(String name) {
		List<Record> records = DBContext.get().findRecord(name);
		if (records == null || records.size() == 0)
			return null;

		final Map<String, String> properties = new HashMap<String, String>();
		final List<PVField> fields = new ArrayList<PVField>();

		if (records.size() > 1) {
			StringBuilder sb = new StringBuilder();
			for (Record rec : records) {
				sb.append("\t");
				sb.append(rec.getName());
				sb.append(": ");
				sb.append(rec.getFile());
				sb.append("\n");
			}
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"{0} matches more than 1 PV:\n {1}",
					new Object[] { name, sb });
		}

		Record rec = records.get(0); // we take first
		if (rec != null) {
			properties.put("Record Name", rec.getName());
			properties.put("Record Type", rec.getType());
			properties.put("File Name", rec.getFile().lastSegment());
			for (Field f : rec.getFields())
				fields.add(new PVField(name + "." + f.getType(), f.getValue()));
		}
		final PVInfo info = new PVInfo(properties, fields);
		Logger.getLogger(getClass().getName()).log(Level.FINE,
				"DB Files Info for {0}: {1}", new Object[] { name, info });
		return info;
	}
	
}
