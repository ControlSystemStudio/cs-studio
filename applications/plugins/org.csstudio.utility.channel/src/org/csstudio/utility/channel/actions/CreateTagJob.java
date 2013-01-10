package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Tag;

import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class CreateTagJob extends Job {
	private Tag.Builder tag;

	public CreateTagJob(String name, Tag.Builder tag) {
		super(name);
		this.tag = tag;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ChannelFinder.getClient().set(tag);
		} catch (ChannelFinderException e) {
			return new Status(Status.ERROR,
					Activator.PLUGIN_ID,
					((ChannelFinderException) e)
							.getStatus()
							.getStatusCode(), e
							.getMessage(), e.getCause());
		}
		return Status.OK_STATUS;
	}

}
