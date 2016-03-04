/**
 *
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Property.Builder.property;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.Property;

import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.csstudio.utility.channelfinder.Activator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Kunal Shroff
 *
 */
public class AddPropertyCommandHandler extends AbstractAdaptedHandler<Channel> {

    public AddPropertyCommandHandler() {
        super(Channel.class);
    }

    @Override
    protected void execute(List<Channel> channels, ExecutionEvent event)
            throws Exception {
        final Shell shell = HandlerUtil.getActiveShell(event);
        Collection<String> existingPropertyNames = null;
        GetAllProperties getAllProperties = new GetAllProperties();
        getAllProperties.addExceptionListener(new ExceptionListener() {

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
                                ErrorDialog
                                        .openError(
                                                shell,
                                                "Error retrieving all the property names.",
                                                exception.getMessage(), status);
                            }
                        });
            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            existingPropertyNames = executor.submit(getAllProperties).get();
        } catch (Exception exception) {
            Display.getDefault().asyncExec(
                    () -> {
                        Status status = new Status(Status.ERROR,
                                Activator.PLUGIN_ID,
                                ((ChannelFinderException) exception)
                                        .getMessage(), exception.getCause());
                        ErrorDialog.openError(shell,
                                "Error retrieving all the tag names.",
                                exception.getMessage(), status);
                    });
        }

        AddPropertyDialog dialog = new AddPropertyDialog(shell,
                existingPropertyNames);
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
            String propertyName = dialog.getPropertyName();
            String propertyValue = dialog.getPropertyValue();

            Property.Builder property = property(propertyName, propertyValue);
            if (existingPropertyNames.contains(propertyName)) {

            } else if (propertyName != null && !propertyName.equals("")) {
                // If the property does not already exist create it
                CreatePropertyDialog createPropertyDialog = new CreatePropertyDialog(shell, propertyName);
                createPropertyDialog.setBlockOnOpen(true);
                if (createPropertyDialog.open() == Window.OK) {
                    Job create = new CreatePropertyJob("Create Property", property(
                            createPropertyDialog.getPropertyName()).owner(
                            createPropertyDialog.getPropertyOwner()));
                    create.schedule();
                } else {
                    return;
                }
            }
            Job job = new AddProperty2ChannelsJob("AddProperty", channels, property);
            job.schedule();
        }
    }

    private class GetAllProperties implements Callable<Collection<String>> {
        private List<ExceptionListener> listeners = new CopyOnWriteArrayList<ExceptionListener>();

        public void addExceptionListener(ExceptionListener listener) {
            this.listeners.add(listener);
        }

        @SuppressWarnings("unused")
        public void removeExceptionListener(ExceptionListener listener) {
            this.listeners.remove(listener);
        }

        @Override
        public Collection<String> call() throws Exception {
            try {
                return ChannelFinder.getClient().getAllProperties();
            } catch (ChannelFinderException e) {
                for (ExceptionListener listener : this.listeners) {
                    listener.exceptionThrown(e);
                }
                return null;
            }
        }

    }
}
