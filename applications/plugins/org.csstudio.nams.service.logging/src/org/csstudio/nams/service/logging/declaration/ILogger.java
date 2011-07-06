
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

package org.csstudio.nams.service.logging.declaration;

/**
 * A service for logging. This service will be avail from the bundles service
 * registry with id <code>Logger.class.getName()</code>. Note: Use the class
 * to identify id to make sure this plugin will be started before use!
 * 
 * Example (Variables with underscore are fields):
 * 
 * <pre>
 * _serviceTrackerLogger = new ServiceTracker(context, Logger.class.getName(),
 * 		null);
 * _serviceTrackerLogger.open();
 * _logger = (Logger) _serviceTrackerLogger.getService();
 * </pre>
 * 
 * @author <a href="mailto:tr@c1-wps.de">Tobias Rathjen</a>, <a
 *         href="mailto:gs@c1-wps.de">Goesta Steen</a>, <a
 *         href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 18.04.2008
 */
public interface ILogger {
	public void logDebugMessage(Object caller, String message);

	public void logDebugMessage(Object caller, String message,
			Throwable throwable);

	public void logErrorMessage(Object caller, String message);

	public void logErrorMessage(Object caller, String message,
			Throwable throwable);

	public void logFatalMessage(Object caller, String message);

	public void logFatalMessage(Object caller, String message,
			Throwable throwable);

	public void logInfoMessage(Object caller, String message);

	public void logInfoMessage(Object caller, String message,
			Throwable throwable);

	public void logWarningMessage(Object caller, String message);

	public void logWarningMessage(Object caller, String message,
			Throwable throwable);
}
