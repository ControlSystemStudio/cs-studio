package org.csstudio.platform.utility.jms;

public interface IJmsComponentFactory {

	public abstract IJmsProducer createJmsProducer(
			String clientId, String[] urlsToConnect);

	public abstract IJmsRedundantReceiver createIjmsRedundantReceiver(
			String id, String url1, String url2);

}