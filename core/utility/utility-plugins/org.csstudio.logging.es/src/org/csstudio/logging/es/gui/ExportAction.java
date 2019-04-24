/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.logging.es.Activator;
import org.csstudio.logging.es.Messages;
import org.csstudio.logging.es.archivedjmslog.MergedModel;
import org.csstudio.logging.es.archivedjmslog.PropertyFilter;
import org.csstudio.logging.es.model.EventLogMessage;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action for exporting current messages from model to file
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExportAction extends Action
{
    /**
     * Dump one message
     * 
     * @param out
     *            PrintStream
     * @param properties
     *            Properties to print
     * @param message
     *            DaMessage
     */
    private static void dumpMessage(final PrintStream out,
            final String[] properties, final EventLogMessage message)
    {
        out.print(message.getPropertyValue(EventLogMessage.ID));
        for (String property : properties)
        {
            String value = message.getPropertyValue(property);
            if (value == null)
            {
                out.print("\t\"\"");
            }
            else
            {
                // We quote the text, so escape inner quotes
                value = value.replaceAll("\"", "\\\"");
                value = value.replaceAll("\n", ";");
                value = value.replaceAll("\t", "    ");
                out.print("\t\"" + value.replaceAll("\"", "\\\"") + "\"");
            }
        }
        out.print("\n");
    }

    /**
     * Get list of all properties that the messages use.
     * <p>
     * Individual messages can have arbitrary properties. To support a
     * spreadsheet-type message dump, this routine gathers all the used
     * properties of all messages.
     * 
     * @param messages
     *            All the messages
     * @return Array of property names
     */
    private static String[] getAllProperties(final EventLogMessage[] messages)
    {
        final Set<String> properties = new TreeSet<>();
        for (EventLogMessage message : messages)
        {
            final Iterator<String> msg_props = message.getProperties();
            while (msg_props.hasNext())
            {
                properties.add(msg_props.next());
            }
        }
        // Convert to array
        return properties.toArray(new String[properties.size()]);
    }

    private final Shell shell;

    private final MergedModel<EventLogMessage> model;

    public ExportAction(final Shell shell,
            final MergedModel<EventLogMessage> model)
    {
        super(Messages.Export);
        this.shell = shell;
        this.model = model;
        setImageDescriptor(AbstractUIPlugin
                .imageDescriptorFromPlugin(Activator.ID, "icons/export.png"));
    }

    @Override
    public void run()
    {
        // Prompt for file name
        UIHelper uiHelper = SingleSourcePlugin.getUIHelper();
        String filename = uiHelper.openOutsideWorkspaceDialog(this.shell,
                SWT.SAVE, null, null);
        if (filename == null)
        {
            return;
        }

        // Open file
        try (PrintStream out = new PrintStream(filename))
        {
            out.println("# CSS Log Messages");
            out.println("#");
            out.println("# Start: " + this.model.getStartSpec());
            out.println("# End: " + this.model.getEndSpec());
            out.println("#");

            PropertyFilter filters[] = this.model.getFilters();
            if (filters != null && filters.length > 0)
            {
                out.println("# Filters:");
                for (PropertyFilter filter : filters)
                {
                    out.println("# " + filter.toString());
                }
            }
            out.print("\n");

            EventLogMessage messages[] = this.model.getMessages();
            String properties[] = getAllProperties(messages);
            out.print("# ID");
            for (String property : properties)
            {
                out.print("\t" + property);
            }
            out.print("\n");
            for (EventLogMessage message : messages)
            {
                dumpMessage(out, properties, message);
            }
        }
        catch (Exception ex)
        {
            MessageDialog.openError(this.shell, Messages.Error,
                    NLS.bind(Messages.ExportErrorFmt, ex.getMessage()));
        }
    }
}
