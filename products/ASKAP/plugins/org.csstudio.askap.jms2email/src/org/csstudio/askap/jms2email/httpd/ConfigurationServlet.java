package org.csstudio.askap.jms2email.httpd;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.askap.jms2email.Activator;
import org.csstudio.askap.jms2email.Application;
import org.csstudio.askap.jms2email.JMSListener;
import org.osgi.framework.Constants;

/**
 * Servlet to display overall status of JMS Log Tool.
 * 
 * @author Xinyu Wu
 */
@SuppressWarnings("nls")
public class ConfigurationServlet extends AbstractServlet {
	/** Required by Serialize interface */
	private static final long serialVersionUID = 1L;

	final private transient Application application;

	private final String appName;

	public ConfigurationServlet(final Application application) {
		this.application = application;

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

		html.openTable(2, new String[] { "Configuration" });
		
		final int httpdPort = application.getHttpdPort();
		html.tableLine(new String[] { "Httpd Port", "" + httpdPort });
		
		final String jmsUrl = application.getJmsUrl();
		html.tableLine(new String[] { "JMS URL", jmsUrl });
		
		final String jmsTopic = application.getJmsTopic();
		html.tableLine(new String[] { "JMS Topic", jmsTopic });
		
		final String jmsFilter = application.getJmsFilters();
		html.tableLine(new String[] { "JMS Filters (currently not supported)", jmsFilter });

		final String mailHost = application.getMailHost();
		html.tableLine(new String[] { "Mail Host", "" + mailHost });

		final String fromAddress = application.getFromAddress();
		html.tableLine(new String[] { "Mail From Address", fromAddress });

		final String toAddress = application.getToAddress();
		html.tableLine(new String[] { "Mail To Address", toAddress });
		
		final String subject = application.getSubject();
		html.tableLine(new String[] { "Mail Subject", subject });

		html.closeTable();
	}
}
