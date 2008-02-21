package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */ 

/**
 * Alarm Simulator is managing the alarm simulator thread.
 * 
 * @author Matthias Clausen
 * 
 */
public class AlarmSimulator {
	private static String COMMAND_START	=	"START";
	private static String COMMAND_STOP 	=	"STOP";
	public static String[] commandList = {COMMAND_START, COMMAND_STOP};
	private boolean isRunning = false;
	private AlarmSimulatorThread thisAlarmSimulatorThread = null;
	private static AlarmSimulator thisAlarmSimulator = null;

	public AlarmSimulator () {
		
	}
	/**
	 * 
	 * @param command START/STOP defined as static here.
	 * @return Error/ Success String
	 */
	public String setCommand (String command) {
		if (command != null && command.equals(COMMAND_START)) {
			if ( !isRunning) {
				thisAlarmSimulatorThread = new AlarmSimulatorThread();
				isRunning = true;
				return "Success: Started!";
			} else {
				return "ERROR: Alredy running";
			}
			
		} else if (command != null && command.equals(COMMAND_STOP)) {
			if ( !isRunning) {
				return "ERROR: Cannot stop -> Not running";
			} else {
				thisAlarmSimulatorThread.setRunning(false);
				isRunning = false;
				return "Success: Stopped!";
			}
		}
		return "ERROR: Undefined command!";
	}
	
	/**
	 * 
	 * @return array of strings with possible commands.
	 */
	public String[] getCommands () {
		String[] commandList = {COMMAND_START, COMMAND_STOP};
		return commandList;
	}
	
	/**
	 * 
	 * @return singleton instance.
	 */
	public static AlarmSimulator getInstance() {
		//
		// get an instance of our singleton
		//
		if ( thisAlarmSimulator == null) {
			synchronized (AlarmSimulator.class) {
				if (thisAlarmSimulator == null) {
					thisAlarmSimulator = new AlarmSimulator();
				}
			}
		}
		return thisAlarmSimulator;
	}

}
