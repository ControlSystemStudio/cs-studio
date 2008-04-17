package org.csstudio.platform.libs.jms;

public interface IJmsComponentFactory {

	public abstract IJmsProducer createJmsProducer(
			String clientId, String[] urlsToConnect);

	public abstract IjmsRedundantReceiver createIjmsRedundantReceiver(
			String id, String url1, String url2);

}