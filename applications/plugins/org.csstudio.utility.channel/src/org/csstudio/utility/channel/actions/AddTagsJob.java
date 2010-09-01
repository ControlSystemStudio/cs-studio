package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.model.XmlChannels;
import gov.bnl.channelfinder.model.XmlTag;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class AddTagsJob extends Job {

	private XmlTag tag;
	private XmlChannels channels;
	
	public AddTagsJob(String name, XmlChannels channels, XmlTag tag) {
		super(name);
		this.channels = channels;
		this.tag = tag;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Adding Tags to channels", IProgressMonitor.UNKNOWN);
//		System.out.println("adding "+tag.getName()+" to "+channels.getChannelNames());
//		ChannelFinderClient.getInstance().addTag(channels.getChannelNames(), tag);		
		monitor.done();
        return Status.OK_STATUS;
	}

}
