
/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
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
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.service.logging.impl;

import org.csstudio.nams.service.logging.declaration.ILogger;
import org.slf4j.LoggerFactory;

/**
 * This implementation simply creates a slf4 Logger for every method.
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @author Markus Moeller
 * @version 0.2, 06.07.2011
 */
public class LoggerImpl implements ILogger {

	public LoggerImpl() {
	    // Nothing to do
	}

	@Override
    synchronized public void logDebugMessage(final Object caller,
			final String message) {
		LoggerFactory.getLogger(caller.getClass()).debug(message);
	}

	@Override
    synchronized public void logDebugMessage(final Object caller,
			final String message, final Throwable throwable) {
	    LoggerFactory.getLogger(caller.getClass()).debug(message, throwable);
	}

	@Override
    synchronized public void logErrorMessage(final Object caller,
			final String message) {
	    LoggerFactory.getLogger(caller.getClass()).error(message);
	}

	@Override
    synchronized public void logErrorMessage(final Object caller,
			final String message, final Throwable throwable) {
	    LoggerFactory.getLogger(caller.getClass()).error(message, throwable);
	}

	@Override
    synchronized public void logFatalMessage(final Object caller,
			final String message) {
	    LoggerFactory.getLogger(caller.getClass()).error(message);
	}

	@Override
    synchronized public void logFatalMessage(final Object caller,
			final String message, final Throwable throwable) {
	    LoggerFactory.getLogger(caller.getClass()).error(message, throwable);
	}

	@Override
    synchronized public void logInfoMessage(final Object caller,
			final String message) {
	    LoggerFactory.getLogger(caller.getClass()).info(message);
	}

	@Override
    synchronized public void logInfoMessage(final Object caller,
			final String message, final Throwable throwable) {
	    LoggerFactory.getLogger(caller.getClass()).info(message, throwable);
	}

	@Override
    synchronized public void logWarningMessage(final Object caller,
			final String message) {
	    LoggerFactory.getLogger(caller.getClass()).warn(message);
	}

	@Override
    synchronized public void logWarningMessage(final Object caller,
			final String message, final Throwable throwable) {
	    LoggerFactory.getLogger(caller.getClass()).warn(message, throwable);
	}
}
