package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Tag;

import java.util.Collection;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import static org.csstudio.utility.channel.CSSChannelUtils.*;

public class AddTagsJob extends Job {

	private Tag.Builder tag;
	private Collection<ICSSChannel> channels;
	
	public AddTagsJob(String name, Collection<ICSSChannel> channels, Tag.Builder tag) {
		super(name);
		this.channels = channels;
		this.tag = tag;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Adding Tags to channels", IProgressMonitor.UNKNOWN);
//		System.out.println("adding "+tag.getName()+" to "+channels.getChannelNames());
		ChannelFinderClient.getInstance().add(tag, getCSSChannelNames(channels));
		monitor.done();
        return Status.OK_STATUS;
	}

}
