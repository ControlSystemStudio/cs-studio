/**
 * 
 */
package org.csstudio.platform.libs.jms;


/**
 * @author Goesta Steen
 *
 */
public class JmsComponentFactory implements IJmsComponentFactory {
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsComponentFactory#createJmsRedundantProducer(java.lang.String, java.lang.String[])
	 */
	public IJmsProducer createJmsProducer(String clientId, String[] urlsToConnect) {
		return new JmsRedundantProducer(clientId, urlsToConnect);
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.jms.IJmsComponentFactory#createIjmsRedundantReceiver(java.lang.String, java.lang.String, java.lang.String)
	 */
	public IJmsRedundantReceiver createIjmsRedundantReceiver(String id, String url1, String url2) {
		return new JmsRedundantReceiver(id, url1, url2);
	}
}
