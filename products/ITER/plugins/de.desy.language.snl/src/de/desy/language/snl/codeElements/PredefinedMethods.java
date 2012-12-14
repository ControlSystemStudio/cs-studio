/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.snl.codeElements;

import de.desy.language.editor.core.ILanguageElements;

/**
 * An enum of the build-in methods of the state notation language. Currently
 * used only for coloring the source.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public enum PredefinedMethods implements ILanguageElements {
	/**
	 * Performs a delay.
	 */
	delay("delay", (short) 1),

	/**
	 * Clears event flags and causes associated event.
	 */
	efClear("efClear", (short) 1),

	/**
	 * Sets event flags and causes associated event.
	 */
	efSet("efSet", (short) 1),

	/**
	 * Checks if the flag was set.
	 */
	efTest("efTest", (short) 1),

	/**
	 * Test the flag, clears event flags and causes associated event.
	 */
	efTestAndClear("efTestAndClear", (short) 1),

	/**
	 * Value of specified macro name.
	 */
	macValueGet("macValueGet", (short) 1),

	/**
	 * Assigns a variable to a PV.
	 */
	pvAssign("pvAssign", (short) 2),

	/**
	 * The count of assigned channels.
	 */
	pvAssignCount("pvAssignCount", (short) 0),

	/**
	 * Checks if the variable is assigned to a PV.
	 */
	pvAssigned("pvAssigned", (short) 1),

	/**
	 * The count of associated channels.
	 */
	pvChannelCount("pvChannelCount", (short) 0),

	/**
	 * The count of connected channels.
	 */
	pvConnectCount("pvConnectCount", (short) 0),

	/**
	 * Checks if the variable is connected to a PV.
	 */
	pvConnected("pvConnected", (short) 1),

	/**
	 * The count of associated variables to the pv.
	 */
	pvCount("pvCount", (short) 1),

	/**
	 * Flushes the PV layers buffer.
	 */
	pvFlush("pvFlush", (short) 0),

	/**
	 * Clears the PV monitor queue.
	 */
	pvFreeQ("pvFreeQ", (short) 1),

	/**
	 * Gets a PV value.
	 */
	pvGet_Channel("pvGet", (short) 1),

	/**
	 * Gets a PV value in given Sync-Mode.
	 */
	pvGet_WithSyncState("pvGet", (short) 2),

	/**
	 * Gets a PV value.
	 */
	pvGetComplete_Channel("pvGetComplete", (short) 1),

	/**
	 * Gets a PV value (monitor queue).
	 */
	pvGetQ("pvGetQ", (short) 1),

	/**
	 * TODO What does this method ??
	 */
	pvIndex("pvIndex", (short) 1),

	/**
	 * Monitors a PV.
	 */
	pvMonitor("pvMonitor", (short) 1),

	/**
	 * Puts a PV value back to channel.
	 */
	pvPut_Channel("pvPut", (short) 1),

	/**
	 * Puts a PV value back to channel in given sync-state.
	 */
	pvPut_WithSyncState("pvPut", (short) 2),

	/**
	 * Puts a PV value back to channel.
	 */
	pvPutComplete("pvPutComplete", (short) 1),

	/**
	 * Puts a PV array-value back to channel.
	 */
	pvPutComplete_ArrayLong("pvPutComplete", (short) 2),

	/**
	 * Puts a PV array-value back to channel.
	 */
	pvPutComplete_ArrayLongLong("pvPutComplete", (short) 3),

	/**
	 * The alarm severity of this pv.
	 */
	pvSeverity("pvSeverity", (short) 1),

	/**
	 * Returns the alarm state of to the pv.
	 */
	pvStatus("pvStatus", (short) 1),

	/**
	 * Stops monitoring a PV.
	 */
	pvStopMonitor("pvStopMonitor", (short) 1),

	/**
	 * The time-stamp of the current value of this pv.
	 */
	pvTimeStamp("pvTimeStamp", (short) 1),

	/**
	 * Prints the text.
	 */
	printf("printf", (short) 1),

	/**
	 * Prints the text.
	 */
	sprintf("sprintf", (short) 1);

	/**
	 * The name of the method (the beginning of the signature).
	 */
	private String _methodName;

	/**
	 * The number of required parameters.
	 */
	private short _paramCount;

	/**
	 * @param methodName
	 *            The name of the method in the source.
	 * @param parameterCount
	 *            The count of parameters.
	 */
	PredefinedMethods(final String methodName, final short parameterCount) {
		this._methodName = methodName;
		this._paramCount = parameterCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getElementName() {
		return this.getMethodName();
	}

	/**
	 * Returns the name of the method (the beginning of the signature).
	 */
	public String getMethodName() {
		return this._methodName;
	}

	/**
	 * The number of required parameters.
	 */
	public short getParamCount() {
		return this._paramCount;
	}
}
