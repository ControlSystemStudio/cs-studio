
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams;

public class SynchObject extends Object {
	
    private int state = 0;
	private long lSetTime = 0;
	
	public SynchObject(int iState, long lLastSetTime) {
		state = iState;
		lSetTime = lLastSetTime;
	}
	
	/**
	 * Get <code>lSetTime</code> only.
	 * <p>
	 * Only use with <code>SynchObject</code> got 
	 * from <code>hasStatusSet()</code>, because it's NOT <code>synchronized</code>.
	 * </p>
	 * 
	 * @return lSetTime		long
	 */
	public long getTime() {
		return lSetTime;
	}

	/**
	 * Get <code>status</code> only.
	 * <p>
	 * Only use with <code>SynchObject</code> got 
	 * from <code>hasStatusSet()</code>, because it's NOT <code>synchronized</code>.
	 * </p>
	 * 
	 * @return state		int
	 */
	public int getStatus() {
		return state;
	}
	
////////////////////////////////////////////////////////////////////////////////
// Three synchonized methods below. ////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get <code>status</code> only.
	 * <p>
	 * This is a <code>synchronized</code> method.
	 * </p>
	 * 
	 * @return state		int
	 */
	public synchronized int getSynchStatus() {
		return state;
	}

	/**
	 * Set <code>status</code> and update <code>lSetTime</code> to the current time.
	 * <p>
	 * This is a <code>synchronized</code> method.
	 * </p>
	 * 
	 * @param status		int
	 */
	public synchronized void setSynchStatus(int status) {
		state = status;
		lSetTime = System.currentTimeMillis();
	}
	
	/**
	 * Checks if <code>status</code> has changed in the last time interval,
	 * if not, set <code>status</code> to <code>errCode</code>.
	 * <p>
	 * This is a <code>synchronized</code> method.
	 * </p>
	 * 
	 * @param actObj			SynchObject - returns <code>status</code> and
	 * 											last <code>lSetTime</code> in
	 * 											a <code>SynchObject</code>.
	 * @param intervalInSec		long - seconds the <code>status</code> has not changed
	 * @param errCode			int - set new <code>status</code> if timeout
	 * @return <code>true</code> if <code>status</code> has changed an everything is o.k.,
     *   and <code>false</code> if <code>status</code> has not changed in the last specified seconds.
	 */
	public synchronized boolean hasStatusSet(SynchObject actObj,
	                                         long intervalInSec,
	                                         int errCode) {
		
	    boolean bRet = true;
		long lCurrentTime = System.currentTimeMillis();

        // if timeout
		if (lSetTime + (intervalInSec*1000) < lCurrentTime) {
			state = errCode;
			lSetTime = lCurrentTime;
			bRet = false;
		}
		
		actObj.state = state;
		actObj.lSetTime = lSetTime;
		
		return bRet;
	}
}
