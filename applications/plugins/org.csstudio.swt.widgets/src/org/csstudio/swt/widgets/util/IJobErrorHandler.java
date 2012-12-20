package org.csstudio.swt.widgets.util;

/**The error handler for exception in job.
 * @author Xihui Chen
 *
 */
public interface IJobErrorHandler {
	
	/**Handle the exception.
	 * @param exception the exception to be handled
	 */
	public void handleError(Exception exception);

}
