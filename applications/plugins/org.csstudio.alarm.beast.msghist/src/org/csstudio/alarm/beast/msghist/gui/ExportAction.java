/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Action for exporting current messages from model to file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExportAction extends Action
{
    final private Shell shell;
    final private Model model;

    public ExportAction(final Shell shell, final Model model)
    {
        super(Messages.Export);
        this.shell = shell;
        this.model = model;
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID,
                                                       "icons/export.png"));
    }

    @Override
    public void run()
    {
        // Prompt for file name
        final FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setText(Messages.ExportTitle);
        dlg.setOverwrite(true);
        final String filename = dlg.open();
        if (filename == null)
            return;

        // Open file
        final PrintStream out;
        try
        {
            out = new PrintStream(filename);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.ExportErrorFmt, ex.getMessage()));
            return;
        }
        // Write messages to file
        try
        {
            out.println("# CSS Log Messages");
            out.println("#");
            out.println("# Start: " + model.getStartSpec());
            out.println("# End: " + model.getEndSpec());
            out.println("#");

            final MessagePropertyFilter filters[] = model.getFilters();
            if (filters != null  &&  filters.length > 0)
            {
                out.println("# Filters:");
                for (MessagePropertyFilter filter : filters)
                {
                    out.println("# '" + filter.getProperty() + "' = '" +
                            filter.getPattern() + "'");
                }
            }
            out.print("\n");

            final Message messages[] = model.getMessages();
            final String properties[] = getAllProperties(messages);
            out.print("# ID");
            for (String property : properties)
                out.print("\t" + property);
            out.print("\n");
            for (Message message : messages)
                dumpMessage(out, properties, message);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.ExportErrorFmt, ex.getMessage()));
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (Exception ex)
            {
                MessageDialog.openError(shell, Messages.Error,
                        NLS.bind(Messages.ExportErrorFmt, ex.getMessage()));
            }
        }
    }

    /** Get list of all properties that the messages use.
     *  <p>
     *  Individual messages can have arbitrary properties.
     *  To support a spreadsheet-type message dump, this routine
     *  gathers all the used properties of all messages.
     *  @param messages All the messages
     *  @return Array of property names
     */
    private String[] getAllProperties(final Message[] messages)
    {
        final Set<String> properties = new TreeSet<String>();
        for (Message message : messages)
        {
            final Iterator<String> msg_props = message.getProperties();
            while (msg_props.hasNext())
                properties.add(msg_props.next());
        }
        // Convert to array
        return properties.toArray(new String[properties.size()]);
    }

    /** Dump one message
     *  @param out PrintStream
     *  @param properties Properties to print
     *  @param message DaMessage
     */
    private void dumpMessage(final PrintStream out, final String[] properties,
                             final Message message)
    {
        out.print(message.getId());
        for (String property : properties)
        {
            String value = message.getProperty(property);
            if (value == null)
                out.print("\t\"\"");
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
}
