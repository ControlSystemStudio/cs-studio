/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import static org.junit.Assert.assertTrue;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author shroffk
 * 
 */
public class AddTagsJobUnitTest {

	private static Logger logger = Logger
			.getLogger("org.csstudio.utility.channel" + "AddTagsJobUnitTest");
	private ChannelFinderClient client;
	private Channel.Builder ch1;
	private Channel.Builder ch2;
	private Tag tag;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			client = ChannelFinder.getClient();
			ch1 = channel("cssUnitTestChannel1").owner("css");
			ch2 = channel("cssUnitTestChannel1").owner("css");
			client.set(ch1);
			client.set(ch2);
			client.set(tag("cssUnitTestTag", "tagOwner"));
		} catch (Exception e) {
			logger.info("Failed to create the channelfinder client"
					+ e.getMessage());
			assertTrue(false);
		}
	}

	@Test
	public void addTagsJobTest() {
		Collection<Channel> channels = new ArrayList<Channel>();
		channels.add(ch1.build());
		channels.add(ch2.build());
		Job job = new AddTag2ChannelsJob("addTags", channels, tag("cssUnitTestTag",
				"tagOwner"));
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
			assertTrue("Failed to Add tags: ",
					client.findByTag("cssUnitTestTag").containsAll(chs));
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		client.deleteChannel("cssUnitTestChannel1");
		client.deleteChannel("cssUnitTestChannel1");
		client.deleteTag("cssUnitTestTag");
	}

}
