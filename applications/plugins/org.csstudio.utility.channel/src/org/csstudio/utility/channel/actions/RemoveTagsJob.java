/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import static org.csstudio.utility.channel.CSSChannelUtils.*;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelFinderException;

import java.util.Collection;
import java.util.Iterator;

import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author shroffk
 * 
 */
public class RemoveTagsJob extends Job {

	private Collection<Channel> channels;
	private Collection<String> selectedTags;

	/**
	 * create a job to remove a set of tags from a set of channels in
	 * channelfinder
	 * 
	 * @param name
	 *            - job name
	 * @param channels
	 *            - list of channels from which tags need to be removed
	 * @param selectedTags
	 *            - list of tag names to be removed
	 */
	public RemoveTagsJob(String name, Collection<Channel> channels,
			Collection<String> selectedTags) {
		super(name);
		this.channels = channels;
		this.selectedTags = selectedTags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Removing Tags from channels",
				IProgressMonitor.UNKNOWN);
		try {
			for (Iterator<String> iterator = selectedTags.iterator(); iterator
					.hasNext();) {
				String tagName = iterator.next();
				monitor.subTask("Removing tag " + tagName);
				ChannelFinder.getClient().delete(tag(tagName),
						getCSSChannelNames(channels));
				monitor.worked(1);
			}
		} catch (ChannelFinderException e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
					((ChannelFinderException) e).getCause().getMessage(),
					e.getCause());
		}
		monitor.done();
		return Status.OK_STATUS;
	}

}
