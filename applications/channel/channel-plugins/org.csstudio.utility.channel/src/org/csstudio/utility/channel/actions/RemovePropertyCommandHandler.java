package org.csstudio.utility.channel.actions;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class RemovePropertyCommandHandler extends
        AbstractAdaptedHandler<Channel> {

    public RemovePropertyCommandHandler() {
        super(Channel.class);
    }

    @Override
    protected void execute(List<Channel> channels, ExecutionEvent event) {
        Shell shell = HandlerUtil.getActiveShell(event);
        ElementListSelectionDialog selectProperties = new ElementListSelectionDialog(shell, new LabelProvider());

        selectProperties.setTitle("Property Selection");

        selectProperties.setMessage("Select the Properties to be removed (* = any string, ? = any char):");
        selectProperties.setMultipleSelection(true);

        Collection<String> existingPropertyNames = ChannelUtil.getPropertyNames(channels);
        selectProperties.setElements(existingPropertyNames.toArray(new String[existingPropertyNames.size()]));
        selectProperties.setBlockOnOpen(true);
        if (selectProperties.open() == Window.OK) {
            Object[] selected = selectProperties.getResult();
            Collection<String> selectedProperties = new TreeSet<String>();
            for (int i = 0; i < selected.length; i++) {
                selectedProperties.add((String) selected[i]);
            }
            if (selectedProperties.size() > 0) {
                Job job = new RemovePropertiesJob("remove properties", channels, selectedProperties);
                job.schedule();
            }
        }
    }

}
