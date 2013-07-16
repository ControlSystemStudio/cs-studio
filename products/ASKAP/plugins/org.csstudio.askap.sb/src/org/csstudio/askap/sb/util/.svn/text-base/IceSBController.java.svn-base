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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;
import org.csstudio.askap.utility.icemanager.IceManager;

import askap.interfaces.schedblock.IObsProgramServicePrx;
import askap.interfaces.schedblock.ISBTemplateServicePrx;
import askap.interfaces.schedblock.ISchedulingBlockServicePrx;
import askap.interfaces.schedblock.NoSuchObsProgramException;
import askap.interfaces.schedblock.ObsProgram;
import askap.interfaces.schedblock.ObsState;
import askap.interfaces.schedblock.SBTemplateStatus;
import askap.interfaces.schedblock.Version;
import askap.interfaces.schedblock.VersionType;

/**
 * @author wu049
 * @created Sep 9, 2010
 *
 */
public class IceSBController {
	private static final Logger logger = Logger.getLogger(IceSBController.class.getName());
	
	private ISBTemplateServicePrx sbTemplateProxy = null;
	private ISchedulingBlockServicePrx sbProxy = null;
	private IObsProgramServicePrx obsProgProxy = null;
	
	private String DEFAULT_OBS_PROGRAM = "";
	
	private static final Map<SchedulingBlock.SBState, ObsState> STATE_MAP = new HashMap<SBState, ObsState>();
		
	static {
		STATE_MAP.put(SBState.DRAFT, ObsState.DRAFT);
		STATE_MAP.put(SBState.SUBMITTED, ObsState.SUBMITTED);
		STATE_MAP.put(SBState.SCHEDULED, ObsState.SCHEDULED);
		STATE_MAP.put(SBState.EXECUTING, ObsState.EXECUTING);
		STATE_MAP.put(SBState.POSTPROCESSING, ObsState.POSTPROCESSING);
		STATE_MAP.put(SBState.PENDINGTRANSFER, ObsState.PENDINGTRANSFER);
		STATE_MAP.put(SBState.COMPLETED, ObsState.COMPLETED);
		STATE_MAP.put(SBState.ERRORED, ObsState.ERRORED);		
	}
		
	/**
	 * 
	 */
	public IceSBController() {
		super();
		
		if (DEFAULT_OBS_PROGRAM.length() == 0) {
			String programName = Preferences.getDefaultObsProgram();
			try {
				obsProgProxy= IceManager.getObsProgramServiceProxy(Preferences.getOBSProgramIceName());
				try {
					ObsProgram program = obsProgProxy.get(programName);
					DEFAULT_OBS_PROGRAM = program.name;
				} catch (NoSuchObsProgramException ex) {
					obsProgProxy.create(new ObsProgram(programName, programName, programName));
					DEFAULT_OBS_PROGRAM = programName;
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not create default ObjProgram: " + programName, e);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#createSB(java.lang.String, long, java.util.Map)
	 */
	public long createSB(String aliasName, String templateName,
			Map<String, String> userConfig) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		long id = sbProxy.create(DEFAULT_OBS_PROGRAM, templateName, aliasName);
		sbProxy.setObsUserParameters(id, userConfig);
		
		return id;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#createTemplate(java.lang.String, java.lang.String, java.util.Map)
	 */
	public long createTemplate(String schemaName, String pythonScript,
			Map<String, String> paramSet) throws Exception {
		if (sbTemplateProxy==null)
			sbTemplateProxy = IceManager.getSBTemplateProxy(Preferences.getSBTemplateIceName());
		
		long id = sbTemplateProxy.create(schemaName, paramSet, pythonScript, SBTemplateStatus.PUBLIC);
		
		return id;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getSBForTemplate(long)
	 */
	public List<SchedulingBlock> getSBForTemplate(String templateName, int majorVersion) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		List<SchedulingBlock> sbList = new ArrayList<SchedulingBlock>();
		long idList[] = sbProxy.getByTemplate(templateName, majorVersion);
		
		if (idList!=null && idList.length>0) {
			for (long id : idList) {
				SchedulingBlock sb = getSchedulingBlock(id);
				sbList.add(sb);
			}
		}
		
		return sbList;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getSchedulingBlock(long)
	 */
	public SchedulingBlock getSchedulingBlock(long id) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		SchedulingBlock sb = new SchedulingBlock();
		sb.setAliasName(sbProxy.getAlias(id));
		sb.setId(id);
		
		Map<String, String> obsVarMap = sbProxy.getObsVariables(id, "");		
		String startTime = obsVarMap.get(Preferences.SB_OBS_VAR_START_TIME);
		if (startTime != null)
			sb.setLastExecutedDate(new Date((long) (Double.parseDouble(startTime) * 1000)));
		
		String duration = obsVarMap.get(Preferences.SB_OBS_VAR_DURATION);
		if (duration != null)
			sb.setLastExecutionDuration((long) Double.parseDouble(duration)*1000);
		
		sb.setExecutedVersion(obsVarMap.get(Preferences.SB_OBS_VAR_VERSION));
		
		
		String errorTime = obsVarMap.get(Preferences.SB_OBS_VAR_ERROR_TIME);
		if (errorTime != null)
			sb.setErrorTimeStamp(new Date((long) (Double.parseDouble(errorTime) * 1000)));
		
		sb.setErrorMessage(obsVarMap.get(Preferences.SB_OBS_VAR_ERROR_MESSAGE));
		
		sb.setParameterMap(sbProxy.getObsParameters(id));
		sb.setState(getObsState(sbProxy.getState(id)));
		sb.setTemplateName(sbProxy.getSBTemplate(id));
		sb.setMajorVersion(sbProxy.getVersion(id));
		
		return sb;
	}
	

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getTemplate(long)
	 */
	public SBTemplate getTemplate(long id) throws Exception {
		if (sbTemplateProxy==null)
			sbTemplateProxy = IceManager.getSBTemplateProxy(Preferences.getSBTemplateIceName());
		
		SBTemplate sbTemplate = new SBTemplate();
		sbTemplate.setId(id);
		
		sbTemplate.setName(sbTemplateProxy.getName(id));
		sbTemplate.setParameterMap(sbTemplateProxy.getObsParamTemplate(id));
		sbTemplate.setPythonScript(sbTemplateProxy.getObsProcedure(id));
		Version version = sbTemplateProxy.getVersion(id);
		sbTemplate.setMajorVersion(version.major);
		sbTemplate.setMinorVersion(version.minor);
		
		return sbTemplate;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getTemplateNames()
	 */
	public List<String> getTemplateNames() throws Exception {
		if (sbTemplateProxy==null)
			sbTemplateProxy = IceManager.getSBTemplateProxy(Preferences.getSBTemplateIceName());

		String names[] = sbTemplateProxy.getAll();
		
		List<String> nameList = new ArrayList<String>();
		for (String s : names)
			nameList.add(s);
			
		return nameList;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getVersions(java.lang.String)
	 */
	public List<SBTemplate> getVersions(String templateName) throws Exception {
		if (sbTemplateProxy==null)
			sbTemplateProxy = IceManager.getSBTemplateProxy(Preferences.getSBTemplateIceName());

		long idList[] = sbTemplateProxy.getByName(templateName);
		
		List<SBTemplate> templateList = new ArrayList<SBTemplate>();
		for (long id : idList)
			templateList.add(getTemplate(id));
		
		return templateList;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#removeSchedulingBlock(long)
	 */
	public void removeSchedulingBlock(long id) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		sbProxy.remove(id);
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#updateTemplate(java.lang.String, java.lang.String, java.util.Map, boolean)
	 */
	public long updateTemplate(String templateName, String pythonScript,
			Map<String, String> paramSet, boolean isMajor) throws Exception {
		if (sbTemplateProxy==null)
			sbTemplateProxy = IceManager.getSBTemplateProxy(Preferences.getSBTemplateIceName());
		
		VersionType versionType = isMajor ? VersionType.MAJOR : VersionType.MINOR;
		long newid = sbTemplateProxy.updateTemplate(templateName, versionType, paramSet, pythonScript);
		
		return newid;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#getSBByState(askap.ui.operatordisplay.util.SchedulingBlock.SBState)
	 */
	public List<SchedulingBlock> getSBByState(SBState states[]) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		List<SchedulingBlock> sbList = new ArrayList<SchedulingBlock>();
		if (states!=null && states.length>0) {
			ObsState obsStates[] = new ObsState[states.length];
			for (int i=0; i<states.length; i++)
				obsStates[i] = STATE_MAP.get(states[i]);
			
			long idList[] = sbProxy.getByState(obsStates);
			for (long id : idList) {
				sbList.add(getSchedulingBlock(id));
			}
		}
		
		
		return sbList;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#setSBState(java.util.List, askap.ui.operatordisplay.util.SchedulingBlock.SBState)
	 */
	public void setSBState(List<Long> sbList, SBState state)
			throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		if (sbList!=null) {
			for (long id : sbList) {
				sbProxy.transition(id, STATE_MAP.get(state));
			}
		}
	}
	
	private static SBState getObsState(ObsState obsState) {
		for (SBState key : STATE_MAP.keySet()) {
			if (STATE_MAP.get(key).equals(obsState))
				return key;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#setSBState(long, askap.ui.operatordisplay.util.SchedulingBlock.SBState)
	 */
	public void setSBState(long id, SBState state) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());

		sbProxy.transition(id, STATE_MAP.get(state));
	}

	/* (non-Javadoc)
	 * @see askap.ui.operatordisplay.controller.SBController#setObsVariables(long, java.util.Map)
	 */
	public void setObsVariables(long id, Map<String, String> valueMap) throws Exception {
		if (sbProxy==null)
			sbProxy = IceManager.getSBServiceProxy(Preferences.getSBIceName());
		
		sbProxy.setObsVariables(id, valueMap);
	}

}
