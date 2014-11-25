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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;
import org.csstudio.askap.utility.AskapHelper;

import askap.util.ParameterSet;

/**
 * @author wu049
 * @created Sep 5, 2010
 *
 */
public class SBTemplateDataModel {
	
	private static final Logger logger = Logger.getLogger(SBTemplateDataModel.class.getName());
	
	// this is a map of Template name to all versions of that template
	// I'm caching the versions so I don't have to keep going back to the server
	TreeMap<String, List<SBTemplate>> templateMap = new TreeMap<String, List<SBTemplate>>();

	// first element is the SB that's currently running
	List<SchedulingBlock> executedSBList = null;
	
	// list SB in the scheduling queue
	List<SchedulingBlock> scheduledList = null;


	IceSBController controller = new IceSBController();

	public SBTemplateDataModel() throws Exception {
		initialise();
	}
	
	public void initialise() throws Exception {
		List<String> tempNameList = controller.getTemplateNames();
		for (String name : tempNameList) {
			List<SBTemplate> tempList = controller.getVersions(name);
			if (tempList!=null)
				templateMap.put(name, tempList);
		}
	}
	
	public Set<String> getTemplateNames() {
		return templateMap.keySet();		
	}

	/**
	 * 
	 * @param templateName
	 */
	public List<Integer> getMajorVersionsForTemplate(String templateName) {		
		List<Integer> versionList = new ArrayList<Integer>();
		List<SBTemplate> templateList = templateMap.get(templateName);
		if (templateList==null)
			return versionList;
		
		for (SBTemplate template : templateList) {
			if (template.getMinorVersion()==0) {
				if (!versionList.contains(template.getMajorVersion()))
					versionList.add(template.getMajorVersion());
			}
		}
		versionList.add(0);
		
		return versionList;
	}
	
	public List<SBTemplate> getMinorVersionsForTemplate(String templateName, long majorVersion) throws Exception {		
		List<SBTemplate> templateList = templateMap.get(templateName);
		List<SBTemplate> minorList = new ArrayList<SBTemplate>();
		
		if (templateList.isEmpty())
			templateList = controller.getVersions(templateName);
		
		for (SBTemplate template : templateList) {
			if (template.getMajorVersion()==majorVersion) {
				minorList.add(template);
			}
		}
		
		return minorList;
	}
	
	public SBTemplate getLatestVersion(String templateName) {
		// getVersions for template
		List<SBTemplate> tempList = templateMap.get(templateName);
		
		if (tempList!=null && tempList.size()>0)
			return tempList.get(0);
		
		return null;
	}
	
	/**
	 * returns the latest Template for the given major version
	 * 
	 * @param templateName
	 * @param majorVersion
	 * @return
	 * @throws Exception
	 */
	public SBTemplate getLatestVersion(String templateName, long majorVersion) throws Exception {
		List<SBTemplate> tempList = templateMap.get(templateName);
					
		// the latest template should be the first one with that major version
		for (SBTemplate temp : tempList) {
			if (temp.getMajorVersion()==majorVersion)
				return temp;
		}
		
		throw new Exception(templateName + " of version " + majorVersion + " does not exist");
	}
	
	public SBTemplate getTemplate(String templateName, String version) throws Exception {
		List<SBTemplate> tempList = templateMap.get(templateName);
		
		// if version is null, return latest version
		if (version == null) {
			if (tempList.size()==0)
				throw new Exception(templateName + " does not have any versions");
			
			return tempList.get(0);
		}
			
		for (SBTemplate temp : tempList) {
			if (temp.getVersion().equals(version))
				return temp;
		}
		
		throw new Exception(templateName + " of version " + version + " does not exist");
	}
	
	
	public SchedulingBlock getSB(long id) throws Exception {
		SchedulingBlock sb = controller.getSchedulingBlock(id);
		controller.getObsVars(sb);
		return sb;
	}
	/**
	 * return all Scheduling Block for a particular template
	 * @param template
	 * @return
	 */
	public List<SchedulingBlock> getSBForTemplate(String templateName, long majorVersion) throws Exception {
		// fetch all sb from DataServic
		List<SchedulingBlock> sbList = controller.getSBForTemplate(templateName, majorVersion);
		return sbList;
	}
	
	
	/**
	 * removes a scheduling block
	 * @param sb
	 */
	public void remove(long id) throws Exception {
		controller.removeSchedulingBlock(id);
	}

	public void setSBState(long id, SBState state) throws Exception {
		controller.setSBState(id, state);
	}
	
	public void setSBState(long id, SBState state, String reason) throws Exception {
		setSBState(id, state);
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put(Preferences.SB_OBS_VAR_ERROR_TIME, "" + (new Date()).getTime()/1000);
		valueMap.put(Preferences.SB_OBS_VAR_ERROR_MESSAGE, reason);
		controller.setObsVariables(id, valueMap);
	}

	/**
	 * @param templateName
	 * @param pythonScript
	 * @param paramSet
	 */
	public void createNewTemplate(String templateName, String pythonScript,
			ParameterSet paramSet) throws Exception {
		if (templateMap.containsKey(templateName))
			throw new Exception("Tempalte already exist!");
		
		long id = controller.createTemplate(templateName, pythonScript, AskapHelper.propertiesToParameterMap(paramSet));
		List<SBTemplate> templateList = templateMap.get(templateName);
		if (templateList == null) {
			templateList = new ArrayList<SBTemplate>();
			templateMap.put(templateName, templateList);
		}
		templateList.add(0, controller.getTemplate(id));
	}
	
	public void updateTemplate(String templateName, String pythonScript, 
			ParameterSet paramSet, boolean isMajor) throws Exception {
		long id = controller.updateTemplate(templateName, pythonScript, AskapHelper.propertiesToParameterMap(paramSet), isMajor);
		List<SBTemplate> templateList = templateMap.get(templateName);
		templateList.add(0, controller.getTemplate(id));
	}

	/**
	 * @param id
	 * @param userConfigModel
	 */
	public SchedulingBlock createSB(String aliasName, SBTemplate template, Map<String, String> userConfig) 
		throws Exception{
		long id = controller.createSB(aliasName, template.getName(), userConfig);
		controller.setSBState(id, SBState.SUBMITTED);
		
		List<SchedulingBlock> sbList = controller.getSchedulingBlocks(new long[]{id});
		if (sbList==null || sbList.size()==0)
			return null;
		
		return sbList.get(0);
	}

	/**
	 * @param schemaName
	 * @return
	 */
	public boolean containsTemplate(String templateName) {
		return templateMap.containsKey(templateName);
	}	
}
