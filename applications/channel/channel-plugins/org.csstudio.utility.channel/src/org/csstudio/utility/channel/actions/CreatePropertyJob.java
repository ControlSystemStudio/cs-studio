/**
 * 
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Property.Builder;

import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Kunal Shroff
 *
 */
public class CreatePropertyJob extends Job {
    private Builder property;

    public CreatePropertyJob(String name, Builder property) {
            super(name);
            this.property = property;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
            try {
                    ChannelFinder.getClient().set(property);
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
