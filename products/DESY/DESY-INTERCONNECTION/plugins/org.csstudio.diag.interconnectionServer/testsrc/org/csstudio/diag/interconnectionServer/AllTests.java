package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.internal.iocmessage.DuplicateMessageDetectorTest;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessageParserTest;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessageTest;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagListTest;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagValuePairTest;
import org.csstudio.diag.interconnectionServer.internal.time.TimeSourceTest;
import org.csstudio.diag.interconnectionServer.internal.time.TimeUtilTest;
import org.csstudio.diag.interconnectionServer.server.ClientRequestTest;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManagerTest;
import org.csstudio.diag.interconnectionServer.server.IocConnectionTest;
import org.csstudio.diag.interconnectionServer.server.LegacyUtilTest;
import org.csstudio.diag.interconnectionServer.server.ReplySenderTest;
import org.csstudio.diag.interconnectionServer.server.SocketMessageSenderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses( { DuplicateMessageDetectorTest.class,
	IocMessageParserTest.class,
	IocMessageTest.class,
	TagListTest.class,
	TagValuePairTest.class,
	
	TimeSourceTest.class,
	TimeUtilTest.class,
	
	ClientRequestTest.class,
	IocConnectionManagerTest.class,
	IocConnectionTest.class,
	LegacyUtilTest.class,
	ReplySenderTest.class,
	SocketMessageSenderTest.class})
public class AllTests {
}