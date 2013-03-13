/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.utility.sddreader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.pvnames.IPVListProvider;
import org.csstudio.pvnames.PVListResult;
import org.csstudio.pvnames.PVNameHelper;

public class SDDPVListProvider implements IPVListProvider {

	private RDBUtil rdb;

	private final String pv_count = "SELECT count(*) FROM functionalvariables WHERE name like ?";
	private final String pv_get = "SELECT name FROM functionalvariables WHERE name like ?";
	
	private PreparedStatement statement_get = null;
	private PreparedStatement statement_count = null;

	public SDDPVListProvider() {
		try {
			rdb = RDBUtil.connect(Preferences.getRDB_Url(),
					Preferences.getRDB_User(), 
					Preferences.getRDB_Password(), 
					true);
			Activator.getLogger().log(Level.INFO,
					"SDDPVListProvider connected to DB: " + Preferences.getRDB_Url());
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public PVListResult listPVs(final String name, final int limit) {
		PVListResult pvList = new PVListResult();
		
		try {
			String sqlPattern = PVNameHelper.convertToSQL(name);
			statement_count = rdb.getConnection().prepareStatement(pv_count);
			statement_count.setString(1, sqlPattern);
			
			final ResultSet result_count = statement_count.executeQuery();
			if(result_count.next())
				pvList.setCount(result_count.getInt(1));
			
			statement_get = rdb.getConnection().prepareStatement(pv_get);
			statement_get.setString(1, sqlPattern);
			statement_get.setMaxRows(limit);
			
			final ResultSet result_get = statement_get.executeQuery();
			while (result_get.next()) {
				pvList.add(result_get.getString(1));
			}
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
		} finally {
			try {
				if (statement_count != null) statement_count.close();
				if (statement_get != null) statement_get.close();
			} catch (SQLException e) {
				Activator.getLogger().log(Level.SEVERE, e.getMessage());
			}
		}
		return pvList;
	}

	@Override
	public void cancel() {
		try {
			if (statement_count != null) statement_count.cancel();
			if (statement_get != null) statement_get.cancel();
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
		} finally {
			try {
				if (statement_count != null) statement_count.close();
				if (statement_get != null) statement_get.close();
			} catch (SQLException e) {
				Activator.getLogger().log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
}
