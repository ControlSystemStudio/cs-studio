package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Tag;

import java.beans.ExceptionListener;
import java.util.Collection;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddTagCommandHandler extends AbstractAdaptedHandler<Channel> {

    public AddTagCommandHandler() {
        super(Channel.class);
    }

    @Override
    protected void execute(List<Channel> channels, ExecutionEvent event) {
        final Shell shell = HandlerUtil.getActiveShell(event);
        Collection<String> existingTagNames = null;
        GetAllTags getAllTags = new GetAllTags();
        getAllTags.addExceptionListener(new ExceptionListener() {

            @Override
            public void exceptionThrown(Exception e) {
                final Exception exception = e;
                PlatformUI.getWorkbench().getDisplay()
                        .asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                Status status = new Status(Status.ERROR,
                                        Activator.PLUGIN_ID,
                                        ((ChannelFinderException) exception)
                                                .getMessage(), exception
                                                .getCause());
                                ErrorDialog.openError(shell,
                                        "Error retrieving all the tag names.",
                                        exception.getMessage(), status);
                            }
                        });
            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            existingTagNames = executor.submit(getAllTags).get();
        } catch (InterruptedException e1) {
        } catch (ExecutionException e1) {
        }

        AddTagDialog dialog = new AddTagDialog(shell, existingTagNames);
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
            String tagName = dialog.getValue();
            Tag.Builder tag = tag(tagName);
            if (existingTagNames.contains(tagName)) {

            } else if (tagName != null && !tagName.equals("")) {
                CreateTagDialog createTagDialog = new CreateTagDialog(shell,
                        tagName);
                createTagDialog.setBlockOnOpen(true);
                if (createTagDialog.open() == Window.OK) {
                    Job create = new CreateTagJob("Create Tag", tag(
                            createTagDialog.getTagName(),
                            createTagDialog.getTagOwner()));
                    create.schedule();
                }else{
                    return;
                }
            }
            Job job = new AddTag2ChannelsJob("AddTags", channels, tag);
            job.schedule();
        }
    }


}
