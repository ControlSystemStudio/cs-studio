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
 * <p>
 * Provides the current time. An implementation of this class which provides
 * the system time can be obtained by calling {@link TimeUtil#systemClock()}.
 * </p>
 * 
 * <p>
 * The purpose of this class is to decouple code that requires the current
 * time from the system clock to improve its testability. When you need an
 * <code>TimeSource</code> in test code, consider using a
 * {@link StubTimeSource} instead of the system clock.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public abstract class TimeSource {

	/**
	 * Returns the current time in milliseconds according to this time source.
	 * 
	 * @return the current time in milliseconds since the epoch.
	 */
	public abstract long now();

	/**
	 * Returns the number of milliseconds that have elapsed since the specified
	 * time, according to this time source.
	 * 
	 * @param time
	 *            the time.
	 * @return the number of milliseconds between <code>time</code> and the
	 *         current time.
	 */
	public final long millisecondsSince(long time) {
		return now() - time;
	}

}
