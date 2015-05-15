/**
 *
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderException;

import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Kunal Shroff
 *
 */
public class ModifyCommandHandler extends AbstractAdaptedHandler<Channel> {

    private ExceptionListener exceptionListener;

    public ModifyCommandHandler() {
        super(Channel.class);
    }

    @Override
    protected void execute(List<Channel> channels, ExecutionEvent event)
            throws Exception {
        final Shell shell = HandlerUtil.getActiveShell(event);
        Collection<String> existingTagNames = Collections.emptyList();
        Collection<String> existingTagProperties = Collections.emptyList();

        GetAllTags getAllTags = new GetAllTags();
        getAllTags.addExceptionListener(exceptionListener);
        GetAllProperties getAllProperties = new GetAllProperties();
        ExecutorService executor = Executors.newScheduledThreadPool(2);
        try {
            existingTagNames = executor.submit(getAllTags).get();
            existingTagProperties = executor.submit(getAllProperties).get();
        } catch (InterruptedException | ExecutionException e1) {
        }

        Channel channel = channels.iterator().next();
        ChannelEditDialog channelEditDialog = new ChannelEditDialog(shell,
                channel, existingTagNames, existingTagProperties){
            @Override
            protected void okPressed() {
                Job job = new ModifyChannelJob("modify channel", channel, getChannel());
                job.schedule();
                close();
            }
        };
        channelEditDialog.open();

        exceptionListener = new ExceptionListener() {

            @Override
            public void exceptionThrown(Exception e) {
                final Exception exception = e;
                PlatformUI
                        .getWorkbench()
                        .getDisplay()
                        .asyncExec(
                                () -> {
                                    Status status = new Status(
                                            Status.ERROR,
                                            Activator.PLUGIN_ID,
                                            ((ChannelFinderException) exception)
                                                    .getMessage(), exception
                                                    .getCause());
                                    ErrorDialog
                                            .openError(
                                                    shell,
                                                    "Error retrieving all the tag names.",
                                                    exception.getMessage(),
                                                    status);
                                });
            }
        };
    }
}
