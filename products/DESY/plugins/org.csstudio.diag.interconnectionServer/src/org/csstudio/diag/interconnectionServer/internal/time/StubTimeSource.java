/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.internal.time;

/**
 * A time source which simply returns a constant value. This class is intended
 * to be used in test code.
 * 
 * @author Joerg Rathlev
 */
public final class StubTimeSource extends TimeSource {
	
	/**
	 * The time.
	 */
	private long _time;

	/**
	 * Creates a new <code>StubTimeSource</code> which will return the specified
	 * time.
	 * 
	 * @param time
	 *            the time that this time source will return when its
	 *            {@link #now()} method is called.
	 */
	public StubTimeSource(long time) {
		_time = time;
	}

	/**
	 * Sets the time that this stub will return when its {@link #now()} method
	 * is called.
	 * 
	 * @param time
	 *            the time in milliseconds.
	 */
	public void setTime(long time) {
		_time = time;
	}

	/**
	 * Returns the time previously set up by the constructor or a call to the
	 * {@link #setTime(long)} method.
	 * 
	 * @return the time in milliseconds.
	 */
	public long now() {
		return _time;
	}
}
