/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.utility.sddreader;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.csstudio.iter.utility.sddreader.data.Field;
import org.csstudio.iter.utility.sddreader.data.Record;
import org.csstudio.platform.utility.rdb.RDBUtil;

public class SDDContext implements Serializable {

	private static final long serialVersionUID = -7537815528361916230L;

	private final String pv_get_fields = "SELECT at.fieldname, fa.attvalue FROM functionalvariables as fv "
			+ "INNER JOIN functionalvarattributes as fa ON fv.id = fa.funcvar_fk "
			+ "INNER JOIN variableattributetypes as at ON at.id = fa.atttype_id "
			+ "WHERE fv.id = ?";
	private final String pv_get_id = "SELECT id, recordType FROM functionalvariables WHERE name = ?";
	
	private final Pattern FIELD_PATTERN = Pattern.compile("^[A-Z0-9]{1,6}$");

	private RDBUtil rdb;

	public SDDContext() {
		try {
			rdb = RDBUtil.connect(Preferences.getRDB_Url(),
					Preferences.getRDB_User(), 
					Preferences.getRDB_Password(),
					true);
			Activator.getLogger().log(Level.INFO,
					"SDDContext connected to DB: " + Preferences.getRDB_Url());
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	public List<Record> findRecord(String name) {
		List<Record> result = new ArrayList<Record>();
		PreparedStatement statement_pv = null;
		PreparedStatement statement_fields = null;
		try {
			statement_pv = rdb.getConnection().prepareStatement(pv_get_id);
			statement_pv.setString(1, name);
			final ResultSet resultSetPV = statement_pv.executeQuery();
			if (resultSetPV.next()) {
				Record r = new Record();
				r.setName(name);
				r.setType(resultSetPV.getString(2));
				
				int id = resultSetPV.getInt(1);
				statement_fields = rdb.getConnection().prepareStatement(pv_get_fields);
				statement_fields.setInt(1, id);
				final ResultSet resultSetFields = statement_fields.executeQuery();
				while (resultSetFields.next()) {
					String type = resultSetFields.getString(1);
					if (FIELD_PATTERN.matcher(type).matches()) {
						Field f = new Field();
						f.setValue(resultSetFields.getString(2));
						f.setType(type);
						r.addField(f);
					}
				}
				
				result.add(r);
			}
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
		} finally {

			try {
				if (statement_fields != null) statement_fields.close();
				if (statement_pv != null) statement_pv.close();
			} catch (SQLException e) {
				Activator.getLogger().log(Level.SEVERE, e.getMessage());
			}
		}
		return result;
	}

}
