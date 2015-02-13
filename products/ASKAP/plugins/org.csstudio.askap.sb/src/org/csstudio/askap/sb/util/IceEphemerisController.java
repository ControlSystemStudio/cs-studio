/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.sb.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.utility.icemanager.IceManager;

import askap.interfaces.ephem.ISourceSearchPrx;
import askap.interfaces.ephem.Source;
import askap.interfaces.ephem.SourceQuery;

/**
 * @author wu049
 * @created Feb 4, 2011
 *
 */
public class IceEphemerisController {

	ISourceSearchPrx sourceSearchProxy;
	
	public IceEphemerisController() {
		
	}
	
	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.EphemerisController#getCategories()
	 */
	public String[] getCategories() throws Exception {
		if (sourceSearchProxy==null)
			sourceSearchProxy = IceManager.getEphemerisProxy(Preferences.getEphemerisIceName());
		
		return sourceSearchProxy.getCatalogues();
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.EphemerisController#searchSource(java.lang.String, java.lang.String)
	 */
	public List<CalibrationSource> searchSource(String name, String category)
			throws Exception {
		if (sourceSearchProxy==null)
			sourceSearchProxy = IceManager.getEphemerisProxy(Preferences.getEphemerisIceName());		
		
		SourceQuery sourceQuery = new SourceQuery();
		sourceQuery.name = (name==null ? "" : name);
		sourceQuery.catalogue = (category==null ? "" : category);
		
		Source sources[] = sourceSearchProxy.query(sourceQuery, (int)Preferences.getSourceSearchMaxMessages(), 0);
		List<CalibrationSource> sourceList = new ArrayList<CalibrationSource>();
		
		if (sources!=null) {
			for (Source s : sources) {
				CalibrationSource source = new CalibrationSource();
				source.name = s.name;
				source.setC1(s.position[0]);
				source.setC2(s.position[1]);
				source.frame = s.frame;
				source.magnitude = "" + s.flux;
				source.catalogue = s.catalogue;
					
				sourceList.add(source);
			}
		}
		
		return sourceList;
	}

}
