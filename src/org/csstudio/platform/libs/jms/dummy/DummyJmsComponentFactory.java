/**
 * 
 */
package org.csstudio.platform.libs.jms.dummy;

import org.csstudio.platform.libs.jms.IJmsComponentFactory;
import org.csstudio.platform.libs.jms.IJmsProducer;
import org.csstudio.platform.libs.jms.IJmsRedundantReceiver;


/**
 * @author Goesta Steen
 *
 */
public class DummyJmsComponentFactory implements IJmsComponentFactory {
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsComponentFactory#createJmsRedundantProducer(java.lang.String, java.lang.String[])
	 */
	public IJmsProducer createJmsProducer(String clientId, String[] urlsToConnect) {
		return new DummyJmsProducer();
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsComponentFactory#createIjmsRedundantReceiver(java.lang.String, java.lang.String, java.lang.String)
	 */
	public IJmsRedundantReceiver createIjmsRedundantReceiver(String id, String url1, String url2) {
		return new DummyJmsRedundantReceiver();
	}
}
