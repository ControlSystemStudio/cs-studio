package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Property.Builder.property;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class RemovePropertiesJob extends Job {

    private Collection<Channel> channels;
    private Collection<String> selectedProperties;

    /**
     * create a job to remove a set of properties from a set of channels in
     * channelfinder
     *
     * @param name
     *            - job name
     * @param channels
     *            - list of channels from which tags need to be removed
     * @param selectedProperties
     *            - list of properties names to be removed
     */
    public RemovePropertiesJob(String name, Collection<Channel> channels,
            Collection<String> selectedProperties) {
        super(name);
        this.channels = channels;
        this.selectedProperties = selectedProperties;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Removing properties from channels",
                IProgressMonitor.UNKNOWN);
        try {
            for (Iterator<String> iterator = selectedProperties.iterator(); iterator.hasNext();) {
                String propertyName = iterator.next();
                monitor.subTask("Removing property " + propertyName);
                Collection<String> channelNames = ChannelUtil.getChannelNames(channels).stream().map(name -> {
                    try {
                        // TODO this is not enough, need to AllowEncodedSlashes to support channel names like sim://noise..
                        return URLEncoder.encode(name, "UTF-8");
                    } catch (Exception e) {

                    }
                    return null;
                }).collect(Collectors.toList());
                ChannelFinder.getClient().delete(property(propertyName), channelNames);
                monitor.worked(1);
            }
        } catch (ChannelFinderException e) {
            return new Status(Status.ERROR, Activator.PLUGIN_ID,
                    ((ChannelFinderException) e).getCause().getMessage(), e.getCause());
        }
        monitor.done();
        return Status.OK_STATUS;
    }

}
