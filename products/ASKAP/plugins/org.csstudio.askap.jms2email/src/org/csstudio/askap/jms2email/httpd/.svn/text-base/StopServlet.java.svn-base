package org.csstudio.askap.jms2email.httpd;

import org.csstudio.askap.jms2email.Application;

/**
 * Servlet to 'stop' the JMS Log Tool.
 * 
 * @author Xinyu Wu
 */
@SuppressWarnings("nls")
public class StopServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	private transient Application application;

	/**
	 * Initialize
	 * 
	 * @param application
	 *            Application to stop when servlet runs
	 */
	public StopServlet(final Application application) {
		this.application = application;
	}

	/** {@inheritDoc} */
	@Override
	protected void fillBody(final HTMLWriter html) {
		html.h1("<h1>Stopping...</h1>\n");
		application.stop();
	}
}
