package org.csstudio.askap.jms2email.httpd;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.askap.jms2email.Activator;
import org.csstudio.askap.jms2email.JMSListener;
import org.osgi.framework.Constants;

/**
 * Servlet to display overall status of JMS Log Tool.
 * 
 * @author Xinyu Wu
 */
@SuppressWarnings("nls")
public class MainServlet extends AbstractServlet {
	/** Required by Serialize interface */
	private static final long serialVersionUID = 1L;

	final private transient JMSListener jmsListener;

	private final String appName;

	public MainServlet(final JMSListener jmsListener) {
		this.jmsListener = jmsListener;

		final Dictionary<String, String> headers = Activator.getInstance()
				.getBundle().getHeaders();
		appName = headers.get(Constants.BUNDLE_NAME) + " "
				+ headers.get(Constants.BUNDLE_VERSION);
	}

	/** Create status page */
	@SuppressWarnings("unchecked")
	@Override
	protected void fillBody(final HTMLWriter html) {
		html.h1(appName);

		html.h2("Message Count: " + jmsListener.getMessageCount());

		final String last_error = jmsListener.getLastError();
		if (last_error.length() > 0) {
			html.openTable(1, "Last Error");
			html.tableLine(last_error);
			html.closeTable();
		}
		final MapMessage map = jmsListener.getLastMessage();
		if (map == null)
			return;

		html.openTable(2, new String[] { "Last JMS Message" });
		try { // Dump all properties of last message
			Enumeration<String> props;
			props = map.getMapNames();
			while (props.hasMoreElements()) {
				final String prop = props.nextElement();
				html.tableLine(new String[] { prop, map.getString(prop) });
			}
		} catch (JMSException e) {
			html.tableLine(new String[] { "Error", e.getMessage() });
		}
		html.closeTable();
	}
}
