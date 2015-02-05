package org.csstudio.platform.utility.jms.asyncreceiver;

import javax.jms.MessageListener;

import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class JmsConnectJob {

	
	private MessageListener _listener;
	private IConnectionMonitor _connectionMonitor;
	private String[] _topics;
	private String _url1;
	private String _url2;

	public JmsConnector get_jmsConnector() {
		return _jmsConnector;
	}

	public void set_jmsConnector(JmsConnector connector) {
		_jmsConnector = connector;
	}

	private JmsConnector _jmsConnector;

	public JmsConnectJob(String[] topics,
			String url1, String url2, MessageListener listener,
			IConnectionMonitor monitor) {
		_listener = listener;
		_connectionMonitor = monitor;
		_topics = topics;
		_url1 = url1;
		_url2 = url2;
	}
	
	/**
	 * Starts the JMS connection.
	 */
	public JmsConnector startJmsConnection() {
//		_log.debug(this, "Starting JMS connection.");
		if (_jmsConnector != null) {
			// There is still an old connection. This shouldn't happen.
			_jmsConnector.disconnect();
//			_log.warn(this, "There was an active JMS connection when starting a new connection");
		}
		
		Job jmsConnectionJob = new Job("Connecting to JMS brokers") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Connecting to JMS servers",
						IProgressMonitor.UNKNOWN);
				_jmsConnector = new JmsConnector(_listener);
				_jmsConnector.addConnectionMonitor(_connectionMonitor);
				try {
					_jmsConnector.connect(_topics, _url1, _url2);
				} catch (JmsConnectionException e) {
					throw new RuntimeException("Could not connect to JMS brokers.", e);
				}
				return Status.OK_STATUS;
			}
		};
		jmsConnectionJob.schedule();
		return _jmsConnector;
	}

}
