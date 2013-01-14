
package org.csstudio.websuite.dao.mock;

import org.csstudio.websuite.dataModel.BasicMessage;

/**A Mock data providet for testing
 * @author ababic
 *
 */
public class MockChannelMessageReciver {

	public static  BasicMessage getChannel(String channel){
		if("channel1".equals(channel)){
			BasicMessage basicMessage = new BasicMessage();
			basicMessage.setProperty("CHANNEL", "channel1");
			basicMessage.setProperty("test1", "channel1working1");
			basicMessage.setProperty("test2", "channel1working2");
			basicMessage.setProperty("test3", "channel1working3");
			basicMessage.setProperty("test4", "channel1working4");
			return basicMessage;
		}else if("channel2".equals(channel)){
			BasicMessage basicMessage = new BasicMessage();
			basicMessage.setProperty("CHANNEL", "channel2");
			basicMessage.setProperty("test1", "channel2working1");
			basicMessage.setProperty("test2", "channel2working2");
			basicMessage.setProperty("test3", "channel2working3");
			basicMessage.setProperty("test4", "channel2working4");
			return basicMessage;
		}
		
		return null;
	}
}
