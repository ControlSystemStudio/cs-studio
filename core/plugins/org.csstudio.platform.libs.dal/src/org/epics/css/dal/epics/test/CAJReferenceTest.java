/**
 * 
 */
package org.epics.css.dal.epics.test;

import com.cosylab.epics.caj.CAJContext;

import gov.aps.jca.Channel;
import junit.framework.TestCase;

/**
 * @author ikriznar
 *
 */
public class CAJReferenceTest extends TestCase {

    private Channel channel;
	private CAJContext context;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		context = new CAJContext();
	    channel = context.createChannel("PV_AI_01");
	    context.pendIO(5.0);
	    assertEquals(Channel.CONNECTED, channel.getConnectionState());
	    channel.put(12.34);
	    context.pendIO(5.0);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		if (!context.isDestroyed())
			context.destroy();
		context = null;
	}
	
	public void test() {
		;
	}

}
