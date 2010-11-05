/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.ChannelUtil.getChannelNames;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.util.Collection;
import java.util.Iterator;

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
	
	public RemoveTagsJob(String name, Collection<Channel> channels,
			Collection<String> selectedTags) {
		super(name);
		this.channels = channels;
		this.selectedTags = selectedTags;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Removing Tags from channels", IProgressMonitor.UNKNOWN);
		for (Iterator<String> iterator = selectedTags.iterator(); iterator.hasNext();) {
			String tagName = iterator.next();
			monitor.subTask("Removing tag "+tagName);
//			ChannelFinderClient.getInstance().removeTag(channels.getChannelNames(), tagName);
			ChannelFinderClient.getInstance().remove(tag(tagName), getChannelNames(channels) );
			monitor.worked(1);
		}
		monitor.done();
        return Status.OK_STATUS;
	}

}
