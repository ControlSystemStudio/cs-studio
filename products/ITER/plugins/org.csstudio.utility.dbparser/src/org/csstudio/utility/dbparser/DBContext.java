/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.utility.dbparser.data.Record;
import org.eclipse.core.resources.IFile;

/**
 * DB context management. This context is available in any level of the
 * application.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DBContext implements Serializable {

	private static final long serialVersionUID = -3296261941300049496L;

	private Map<String, List<Record>> records;

	private static final DBContext context = new DBContext();

	/**
	 * Singleton = private constructor
	 */
	private DBContext() {
		records = new TreeMap<String, List<Record>>();
	}

	/**
	 * Get the instance of {@link DBContext}..
	 */
	public static DBContext get() {
		return context;
	}

	public List<Record> findRecord(String name) {
		List<Record> result = new ArrayList<Record>();
		for (String rec : records.keySet()) {
			// replace macro by .*
			final String regexp = rec.replaceAll("\\$\\([a-zA-Z0-9]+\\)", ".*");
			Pattern p = Pattern.compile(regexp);
			Matcher m = p.matcher(name);
			if (m.matches())
				result.addAll(records.get(rec));
		}
		return result;
	}

	public Record getRecord(String name) {
		if (records.get(name) == null)
			return null;
		return records.get(name).get(0);
	}

	public Set<String> listRecords() {
		return records.keySet();
	}

	public void addRecord(IFile file, Record record) {
		record.setFile(file.getFullPath());
		if (records.get(record.getName()) == null)
			records.put(record.getName(), new ArrayList<Record>());
		records.get(record.getName()).add(record);
	}

	public void removeFile(IFile file) {
		for (List<Record> list : records.values()) {
			Iterator<Record> it = list.iterator();
			while (it.hasNext()) {
				Record r = it.next();
				if (r.getFile().equals(file))
					it.remove();
			}
		}
		Iterator<Map.Entry<String, List<Record>>> it = records.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<Record>> entry = it.next();
			if (entry.getValue().isEmpty())
				it.remove();
		}
	}

}
