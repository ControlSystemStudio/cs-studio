/**
 *
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import static gov.bnl.channelfinder.api.Property.Builder.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 *
 * TODO need to improve the update/modify method which currently updates the
 * channel one tag/property at a time in order to accommodate for authorization
 * restrictions.
 *
 * A better method would be to make a single call with the entire modified channel
 *
 * @author Kunal Shroff
 *
 */
public class ModifyChannelJob extends Job{

    private static Logger logger = Logger.getLogger(ModifyChannelJob.class.getName());
    private final Channel orginalChannel;
    private final Channel newChannel;

    public ModifyChannelJob(String name, Channel orginalChannel, Channel newChannel) {
        super(name);
        this.orginalChannel = orginalChannel;
        this.newChannel = newChannel;
    }

    /**
     *
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        ChannelFinderClient client = ChannelFinder.getClient();
        String channelName = orginalChannel.getName();
        // new tags added to the channel
        Set<String> updateTags = new HashSet<String>(newChannel.getTagNames());
        updateTags.removeAll(orginalChannel.getTagNames());
        for (String tag : updateTags) {
            logger.info(() -> "add tag:" + tag );
            client.update(tag(tag), channelName);
        }

        // tags removed from the channel
        Set<String> removedTags = new HashSet<String>(orginalChannel.getTagNames());
        removedTags.removeAll(newChannel.getTagNames());
        for (String tag : removedTags) {
            logger.info(() -> "removed tag: "+ tag);
            client.delete(tag(tag), channelName);
        }

        Collection<String> allProperties = new HashSet<String>();
        allProperties.addAll(orginalChannel.getPropertyNames());
        allProperties.addAll(newChannel.getPropertyNames());
        for (String propertyName : allProperties) {
            if (orginalChannel.getPropertyNames().contains(propertyName)
                    && !newChannel.getPropertyNames().contains(propertyName)) {
                // This property has been removed
                logger.info(() -> "removed property: " + newChannel.getProperty(propertyName).toString());
                client.delete(property(propertyName), channelName);
            } else if (newChannel.getPropertyNames().contains(propertyName)
                    && !orginalChannel.getPropertyNames()
                            .contains(propertyName)) {
                // This property has been added
                logger.info(() -> "added property" + newChannel.getProperty(propertyName).toString());
                client.update(property(newChannel.getProperty(propertyName)), channelName);
            } else if (!newChannel.getProperty(propertyName).equals(
                    orginalChannel.getProperty(propertyName))) {
                // A property with modified values
                logger.info(() -> "modified property" + newChannel.getProperty(propertyName).toString());
                client.update(property(newChannel.getProperty(propertyName)), channelName);
            }
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}
