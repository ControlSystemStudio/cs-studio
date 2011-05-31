/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shroffk
 * 
 */
public class RemoveTagsJobUnitTest {

	private static Logger logger = Logger
			.getLogger("org.csstudio.utility.channel" + "AddTagsJobUnitTest");
	private ChannelFinderClient client;
	private Channel.Builder ch1;
	private Channel.Builder ch2;
	private Tag.Builder tag;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public void setUp() throws Exception {
//		final TestProperties settings = new TestProperties();
		try {
			client = ChannelFinderClient.getInstance();
			tag = tag("cssUnitTestTag", "tagOwner");
			ch1 = channel("cssUnitTestChannel1").owner("css").with(tag);
			ch2 = channel("cssUnitTestChannel1").owner("css").with(tag);
			client.add(tag);
			client.add(ch1);
			client.add(ch2);
		} catch (Exception e) {
			logger.info("Failed to create the channelfinder client"
					+ e.getMessage());
			assertTrue(false);
		}
	}

	@Test
	public void removeTagsJobTest() {
		Collection<Channel> channels = new ArrayList<Channel>();
		channels.add(ch1.build());
		channels.add(ch2.build());
		Job job = new RemoveTagsJob("remove tags", channels,
				ChannelUtil.getTagNames(ch1.build()));
		job.schedule();
		try {
			// TODO not have a static def for the amount of time needed to
			// complete the update
			job.getThread().join(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			assertTrue("The Job is taking too long.", false);
		}
		if (job.getResult() == Status.OK_STATUS) {
			Collection<Channel> chs = new ArrayList<Channel>();
			chs.add(ch1.build());
			chs.add(ch2.build());
			assertNull("Failed to Add tags: ",
					client.findChannelsByTag("cssUnitTestTag"));
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public void tearDown() throws Exception {
		client.remove(channel("cssUnitTestChannel1").owner("css"));
		client.remove(channel("cssUnitTestChannel1").owner("css"));
		client.deleteTag("cssUnitTestTag");
	}

}
