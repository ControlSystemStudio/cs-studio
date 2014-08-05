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
	
	// first element is the SB that's currently running
	List<SchedulingBlock> executedSBList = new ArrayList<SchedulingBlock>();
	
	// list SB in the scheduling queue
	List<SchedulingBlock> scheduledList = new ArrayList<SchedulingBlock>();

	IceSBController sbController = new IceSBController();
	IceExecutiveController executiveController = new IceExecutiveController();
	IceMonitoringController executiveMonitorController = null;
	IceExecutiveLogController executivelogController = null;
	DataChangeListener dataChangeListener = null;
	
	boolean keepRunning = true;
	Object pollingThreadLock = new Object();
	
	int numberOfRetries = 1;
	
	public SBDataModel() {
		executiveMonitorController = new IceMonitoringController(Preferences.getExecutiveMonitorIceName());
		executivelogController = new IceExecutiveLogController(Preferences.getExecutiveLogSubscriberName(),
										Preferences.getExecutiveLogTopicName(),
										Preferences.getExecutiveLogTag());
	}

	public void setDataChangeListener(DataChangeListener listener) {
		this.dataChangeListener = listener;
		executiveController.setDataChangedListener(dataChangeListener);
	}
	
	public int getExecutedSBCount() {
		return executedSBList.size();
	}
	
	public int getScheduledSBCount() {
		return scheduledList.size();
	}
	
	public SchedulingBlock getExecutedSBAt(int index) {
		if (index<0 || index>executedSBList.size())
			return null;
		
		return executedSBList.get(index);
	}
	
	public SchedulingBlock getScheduledSBAt(int index) {
		if (index<0 || index>scheduledList.size())
			return null;
		
		return scheduledList.get(index);
	}
	
	public void startSBPollingThread(final SBListener pollingThreadListener) {
		Thread sbPollingThread = new Thread(new Runnable() {		
			public void run() {
				synchronized (pollingThreadLock) {
					while (keepRunning) {				
						try {
							String states[] = pollingThreadListener.getStates();
							
							boolean b1 = processScheduledList();
							boolean b2 = processFinishedList(states);
							if (b1 || b2) {
								DataChangeEvent event = new DataChangeEvent();
								pollingThreadListener.dataChanged(event);
							}
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

	public void addPointListener(final String pointNames[], final MonitorPointListener listener) {		
		try {
			executiveMonitorController.addMonitorPointListener(pointNames, listener);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not get executive monitoring points " + e.getMessage());
		}		
	}
	
	public void removePointListener(final String pointNames[], final MonitorPointListener listener) {		
		try {
			executiveMonitorController.removeMonitorPointListener(pointNames, listener);
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
		executiveMonitorController.removeAllListeners();
	}

	public void interruptPollingThread() {
		synchronized (pollingThreadLock) {
			pollingThreadLock.notify();
		}
	}
	
	protected boolean processFinishedList(String stateStr[]) throws Exception {
		SBState states[] = new SBState[stateStr.length];
		for (int i=0; i<stateStr.length; i++)
			states[i] = SBState.valueOf(stateStr[i]);
		
		// if scheduled list has changed, notify the listener
		// also only display the given max number of sb
		List<SchedulingBlock> finishedSB = getSBByState(states, Preferences.getSBExecutionMaxNumberSB());
		
		List<SchedulingBlock> sbList = getSBByState(new SBState[] {SBState.EXECUTING});
		SchedulingBlock executingSB = null;
		if (sbList!=null && sbList.size()>0)
			executingSB = sbList.get(0);

		if (finishedSB!=null) {
			if (executingSB!=null)
				finishedSB.add(0, executingSB);
			
			if (finishedSB.size()==executedSBList.size()) {
				for (int i=0; i<finishedSB.size(); i++) {
					if (!finishedSB.get(i).equals(executedSBList.get(i))) {
						executedSBList = finishedSB;
						return true;
					}
				}					
			} else {
				executedSBList = finishedSB;
				return true;
			}
		}
		
		return false;
	}

	protected boolean processScheduledList() throws Exception {
		// if scheduled list has changed, notify the listener
		List<SchedulingBlock> newScheduledList = getSBByState(new SBState[]{SBState.SCHEDULED});
		if (newScheduledList!=null) {
			if (newScheduledList.size()==scheduledList.size()) {
				for (int i=0; i<newScheduledList.size(); i++) {
					if (newScheduledList.get(i).getId() != scheduledList.get(i).getId()) {
						scheduledList = newScheduledList;
						return true;
					}
				}					
			} else {
				scheduledList = newScheduledList;
				return true;
			}
		}
		
		return false;
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
	public List<SchedulingBlock> getSBByState(SBState states[]) throws Exception {
		return sbController.getSBByState(states);
	}

	/**
	 * @param 
	 * @return maxNumber of sbs for the given list of sbstates
	 */
	public List<SchedulingBlock> getSBByState(SBState states[], long maxNumber) throws Exception {
		return sbController.getSBByState(states, maxNumber);
	}

	/**
	 * @param allList
	 * @param submitted
	 */
	public void setSBState(long id, SBState state) throws Exception {
		sbController.setSBState(id, state);
	}
}
