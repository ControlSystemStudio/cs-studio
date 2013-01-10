package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Tag;

import java.util.Collection;

import javax.print.attribute.standard.Severity;

import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import static org.csstudio.utility.channel.CSSChannelUtils.*;

public class AddTag2ChannelsJob extends Job {

	private Tag.Builder tag;
	private Collection<Channel> channels;
	
	/**
	 * create a job to add a tag _tag_ to a group of channels
	 * 
	 * @param name - job name
	 * @param channels - collection of channels to which the tag is to be added
	 * @param tag - builder of the the tag to be added
	 */
	public AddTag2ChannelsJob(String name, Collection<Channel> channels, Tag.Builder tag) {
		super(name);
		this.channels = channels;
		this.tag = tag;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Adding Tags to channels", IProgressMonitor.UNKNOWN);		
		try {
			ChannelFinder.getClient().update(tag, getCSSChannelNames(channels));
		} catch (ChannelFinderException e) {
			return new Status(Status.ERROR,
					Activator.PLUGIN_ID,
					((ChannelFinderException) e)
							.getStatus()
							.getStatusCode(), e
							.getMessage(), e.getCause());
		}		
		monitor.done();
        return Status.OK_STATUS;
	}

}
