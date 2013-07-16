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
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;

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
	DataChangeListener dataChangeListener = null;
	
	boolean keepRunning = true;
	Object pollingThreadLock = new Object();
	
	public SBDataModel() {
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
	
	public void startSBPollingThread(final DataChangeListener pollingThreadListener) {
		Thread sbPollingThread = new Thread(new Runnable() {		
			public void run() {
				synchronized (pollingThreadLock) {
					while (keepRunning) {				
						try {
							boolean b1 = processScheduledList();
							boolean b2 = processFinishedList();
							if (b1 || b2) {
								DataChangeEvent event = new DataChangeEvent();
								pollingThreadListener.dataChanged(event);
							}
							
							pollingThreadLock.wait(Preferences.getSBExecutionStatePollingPeriod());
						} catch (Exception e) {
							logger.log(Level.WARNING, "Could not poll SB state" + e.getMessage());
						}
					}
					
				}
			}
		});
		keepRunning = true;
		sbPollingThread.start();		
	}
	
	public void interruptPollingThread() {
		synchronized (pollingThreadLock) {
			pollingThreadLock.notify();
		}
	}
	
	protected boolean processFinishedList() throws Exception {
		// if scheduled list has changed, notify the listener
		List<SchedulingBlock> newFinishedList = getSBByState(new SBState[]{SBState.COMPLETED, SBState.ERRORED, SBState.EXECUTING});
		
		if (newFinishedList!=null) {
			// move the EXECUTING scheduling block to the beginning
			SchedulingBlock executingSB = null;
			for (SchedulingBlock sb : newFinishedList) {
				if (sb.getState().equals(SBState.EXECUTING))
					executingSB = sb;			
			}
			
			if (executingSB!=null) {
				newFinishedList.remove(executingSB);
				newFinishedList.add(0, executingSB);
			}
			
			if (newFinishedList.size()==executedSBList.size()) {
				for (int i=0; i<newFinishedList.size(); i++) {
					if (!newFinishedList.get(i).equals(executedSBList.get(i))) {
						executedSBList = newFinishedList;
						return true;
					}
				}					
			} else {
				executedSBList = newFinishedList;
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

	public void stopSBPollingThread() {
		keepRunning = false;
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
	 * @param scheduled
	 * @return
	 */
	public List<SchedulingBlock> getSBByState(SBState states[]) throws Exception {
		return sbController.getSBByState(states);
	}

	/**
	 * @param allList
	 * @param submitted
	 */
	public void setSBState(long id, SBState state) throws Exception {
		sbController.setSBState(id, state);
	}
}
