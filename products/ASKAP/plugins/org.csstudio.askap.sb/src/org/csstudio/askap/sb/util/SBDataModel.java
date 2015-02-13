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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.Preferences;
import org.csstudio.askap.sb.SBExecutionView.SBListener;
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;

/**
 * @author wu049
 * @created Sep 5, 2010
 *
 */
public class SBDataModel {
	
	private static final Logger logger = Logger.getLogger(SBDataModel.class.getName());
	private static final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

	
	// a cached list of all SB
	private static TreeSet<SchedulingBlock> sbList = new TreeSet<SchedulingBlock>(new SchedulingBlock.SBExecutionTimeComparator());;
	private static Date lastUpdate = null;

	IceSBController sbController = new IceSBController();
	IceExecutiveController executiveController = new IceExecutiveController();
	
	// map of adaptor name to their monitorController
	Map<String, IceMonitoringController> monitorControllerMap = new HashMap<String, IceMonitoringController>();
	
	IceExecutiveLogController executivelogController = null;
	DataChangeListener dataChangeListener = null;
	
	boolean keepRunning = true;
	Object pollingThreadLock = new Object();
	
	int numberOfRetries = 1;
	
	public SBDataModel() {
		executivelogController = new IceExecutiveLogController(Preferences.getExecutiveLogSubscriberName(),
										Preferences.getExecutiveLogTopicName(),
										Preferences.getExecutiveLogOrigin());
		
		ISO8601.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		// no need to refresh the sbList if it's not empty
		if (!sbList.isEmpty())
			return;
		
		List<SchedulingBlock> updatedList = null;
		try {
			updatedList = getSBByState(getStates(), "");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not retrieve all the scheduling blocks");
		}
		
		if (updatedList!=null) {
			for (SchedulingBlock sb : updatedList) {
				sbList.add(sb);
			}		
		}
	}

	public void setDataChangeListener(DataChangeListener listener) {
		this.dataChangeListener = listener;
		executiveController.setDataChangedListener(dataChangeListener);
	}
	
	public void startSBPollingThread(final SBListener pollingThreadListener) {
		Thread sbPollingThread = new Thread(new Runnable() {		
			public void run() {
				synchronized (pollingThreadLock) {
					while (keepRunning) {				
						try {
							pollingThreadListener.updateScheduledTable(processScheduledList());
							String states[] = pollingThreadListener.getStates();
							pollingThreadListener.updateExecutedTable(processFinishedList(states));

							refreshSBList();							

							numberOfRetries = 1;
							pollingThreadLock.wait(Preferences.getSBExecutionStatePollingPeriod());
							
						} catch (Exception e) {
							try {
								pollingThreadLock.wait(Preferences.getSBExecutionStatePollingPeriod()*numberOfRetries);
								numberOfRetries++;
								if (numberOfRetries>10)
									numberOfRetries = 10;
							} catch (Exception ex) {
								logger.log(Level.INFO, "Wait interrupted " + e.getMessage());
							}
							logger.log(Level.WARNING, "Could not poll SB state" + e.getMessage());
						}
					}
					
				}
			}
		});
		keepRunning = true;
		sbPollingThread.start();		
	}

	public void addPointListener(String adapterName, String pointNames[], final MonitorPointListener listener) {		
		try {
			IceMonitoringController monitorController = monitorControllerMap.get(adapterName);
			if (monitorController==null) {
				monitorController = new IceMonitoringController(adapterName);
				monitorControllerMap.put(adapterName, monitorController);
			}
			monitorController.addMonitorPointListener(pointNames, listener);
			
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not get executive monitoring points " + e.getMessage());
		}		
	}
	
	public void removePointListener(String adapterName, final String pointNames[], final MonitorPointListener listener) {		
		try {
			IceMonitoringController monitorController = monitorControllerMap.get(adapterName);
			if(monitorController!=null)
				monitorController.removeMonitorPointListener(pointNames, listener);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not get executive monitoring points " + e.getMessage());
		}		
	}
	
	public void startExecutiveLogSubscriber(final DataChangeListener subscriber) {
		try {
			executivelogController.subscribe(subscriber);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not subscribe to Executive Log" + e.getMessage());
		}
	}	
	
	public void stopUpdates() {
		keepRunning = false;
		
		// stop subscriber too
		try {
			executivelogController.stop();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not stop subscribing to Executive Log" + e.getMessage());
		}
		
		// remove listeners to all monitor points
		for (IceMonitoringController monitorController : monitorControllerMap.values())
			monitorController.removeAllListeners();
	}

	public void interruptPollingThread() {
		synchronized (pollingThreadLock) {
			pollingThreadLock.notify();
		}
	}
	
	private void refreshSBList() {
		String lastUpdateStr = "";
		Date beforeUpdate = new Date();

		if (lastUpdate!=null)
			lastUpdateStr = ISO8601.format(lastUpdate);

		try {
			List<SchedulingBlock> updatedList = getSBByState(getStates(), lastUpdateStr);
			sbController.getObsVar(updatedList);
			
			lastUpdate = beforeUpdate;

			if (updatedList==null || updatedList.isEmpty())
				return;
			
			// remove all the SchedulingBlock in the updatedList first
			// then add them all again to make sure order and info are correct
			List<Long> sbIdList = new ArrayList<Long>();
			for (SchedulingBlock sb : updatedList) {
				sbIdList.add(sb.id);
			}
			
			SchedulingBlock tempList[] = sbList.toArray(new SchedulingBlock[]{});
			for (SchedulingBlock sb : tempList) {
				if (sbIdList.contains(sb.id))
					sbList.remove(sb);
			}
			
			for (SchedulingBlock sb : updatedList) {
				sbList.add(sb);
			}		
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not retrieve SB: ", e);
		}
	}
	 
	/*
	 * works out if the given state is in the list of stateStr
	 */
	private boolean inState(String stateList[], SBState state) {
		for (String s : stateList) {
			if (s.equals(state.name()))
				return true;
		}
		
		return false;
	}
	
	protected List<SchedulingBlock> processFinishedList(String stateList[]) throws Exception {
		// cull by states
		List<SchedulingBlock> finishedList = new ArrayList<SchedulingBlock>();
		
		for (Iterator<SchedulingBlock> iter = sbList.descendingIterator(); iter.hasNext();) {
			SchedulingBlock sb = iter.next();
			if (inState(stateList, sb.state))
				finishedList.add(sb);
		}
		
		return finishedList;
	}

	protected List<SchedulingBlock> processScheduledList() throws Exception {

		List<SchedulingBlock> scheduledList = new ArrayList<SchedulingBlock>();
		List<SchedulingBlock> runningList = new ArrayList<SchedulingBlock>();

		for (Iterator<SchedulingBlock> iter = sbList.iterator(); iter.hasNext();) {
			SchedulingBlock sb = iter.next();
			if (sb.state.equals(SBState.SCHEDULED))
				scheduledList.add(sb);
			
			if (sb.state.equals(SBState.EXECUTING))
				runningList.add(sb);
		}
		scheduledList.addAll(0, runningList);

		return scheduledList;
	}

	/**
	 * 
	 */
	public void stop() throws Exception {
		executiveController.stop();
	}

	/**
	 * 
	 */
	public void start() throws Exception {
		executiveController.start();
	}

	/**
	 * 
	 */
	public void abort() throws Exception {
		executiveController.abort();
	}

	/**
	 * @param 
	 * @return sbs for the given list of sbstates
	 */
	public List<SchedulingBlock> getSBByState(SBState states[], String lastUpdate) throws Exception {
		return sbController.getSBByState(states, lastUpdate);
	}

	/**
	 * @param allList
	 * @param submitted
	 */
	public void setSBState(long id, SBState state) throws Exception {
		sbController.setSBState(id, state);
	}
	
	private SBState[] getStates() {
		// get all the states except 	DRAFT and RETIRED
		SBState[] allStates = SBState.values();
		SBState[] states = new SBState[allStates.length-2];
		int i = 0;
		for (SBState state : allStates) {
			if (SBState.RETIRED.equals(state) || 
					SBState.DRAFT.equals(state)) {
				// skip
				continue;
			}
			
			states[i] = state;
			i++;
		}
		
		return states;
	}
}
