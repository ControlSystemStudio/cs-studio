package org.csstudio.alarm.dbaccess;

import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;

public class MessagePropertyTypeContentTest {

	@Test
	public void testMsgPropertyTypeAccess() {
		if (MessagePropertyTypeContent.getPropertyIDMapping() == null) {
			MessagePropertyTypeContent.readMessageTypes();
		}
		final Set<Entry<String, String>> msg_type = MessagePropertyTypeContent
				.getPropertyIDMapping().entrySet();
		for (final Entry<String, String> entry : msg_type) {
			// System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
		}
	}
}
