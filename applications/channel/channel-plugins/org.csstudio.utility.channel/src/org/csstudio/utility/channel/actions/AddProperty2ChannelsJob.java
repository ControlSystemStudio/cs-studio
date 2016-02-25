/**
 *
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;

import java.util.Collection;

import org.csstudio.utility.channel.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * @author Kunal Shroff
 *
 */
public class AddProperty2ChannelsJob extends Job {

    private Property.Builder property;
    private Collection<Channel> channels;

    /**
     * create a job to add a tag _tag_ to a group of channels
     *
     * @param name - job name
     * @param channels - collection of channels to which the tag is to be added
     * @param tag - builder of the the tag to be added
     */
    public AddProperty2ChannelsJob(String name, Collection<Channel> channels, Property.Builder property) {
        super(name);
        this.channels = channels;
        this.property = property;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Adding Properties to channels", IProgressMonitor.UNKNOWN);
        try {
            ChannelFinder.getClient().update(property, ChannelUtil.getChannelNames(channels));
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
