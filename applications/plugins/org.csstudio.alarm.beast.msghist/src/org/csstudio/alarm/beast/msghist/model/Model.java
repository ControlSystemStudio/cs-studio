/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.Preferences;
import org.csstudio.apputil.time.StartEndTimeParser;

/** Model of CSS log messages.
 *  <p>
 *  Handles async. database requests, notifies listeners
 *  on change.
 *
 *  @author Kay Kasemir
 */
public class Model
{
    final private String url;
    final private String user;
    final private String password;
    final private String schema;
    private volatile Message messages[] = new Message[0];
    private CopyOnWriteArrayList<ModelListener> listeners =
        new CopyOnWriteArrayList<ModelListener>();
    private String start_spec = Preferences.getDefaultStart();
    private String end_spec = "now"; //$NON-NLS-1$
    private MessagePropertyFilter filters[] = new MessagePropertyFilter[0];
    private int max_properties;
    private GetMessagesJob message_job;

    /** Constructor
     *  @param url URL for RDB that holds log messages
     *  @param user RDB user name
     *  @param password RDB password
     *  @param schema Database schema ending in "." or "" if not used
     *  @throws Exception on error
     */
    public Model(final String url, final String user, final String password,
            final String schema, final int max_properties) throws Exception
    {
        this.url = url;
        this.user = user;
        this.password = password;
        this.schema = schema;
        this.max_properties = max_properties;
    }

    /** Add Model Listener */
    public void addListener(final ModelListener listener)
    {
        listeners.add(listener);
    }

    /** Remove Model Listener */
    public void removeListener(final ModelListener listener)
    {
        listeners.remove(listener);
    }

    /** @return Start time specification
     *  @see StartEndTimeParser
     */
    public String getStartSpec()
    {
        return start_spec;
    }

    /** @return End time specification
     *  @see StartEndTimeParser
     */
    public String getEndSpec()
    {
        return end_spec;
    }

    /** Set model's time range.
     *  Model will retrieve messages for given time and other current settings
     *  in a background thread and then notify listeners.
     *
     *  @param start_spec Start time specification
     *  @param end_spec End time specification
     *  @throws Exception on error
     *  @see StartEndTimeParser
     */
    public void setTimerange(final String start_spec, final String end_spec)
        throws Exception
    {
        this.start_spec = start_spec;
        this.end_spec = end_spec;
        launchQuery();
    }

    /** @return Current filter settings or <code>null</code> */
    public MessagePropertyFilter[] getFilters()
    {
        return filters;
    }

    /** Set model's filters.
     *  Model will retrieve messages for given filters and other current settings
     *  in a background thread and then notify listeners.
     *
     *  @param filters Filters to use (<code>null</code> for 'no filters)
     *  @throws Exception on error
     */
    public void setFilters(final MessagePropertyFilter filters[])
        throws Exception
    {
        this.filters = filters;
        launchQuery();
    }

    /** Launch RDB query with current settings. */
    private void launchQuery() throws Exception
    {
        // Cancel a job that might already be running
        if (message_job != null)
            message_job.cancel();

        // Start new job
        final StartEndTimeParser times =
            new StartEndTimeParser(start_spec, end_spec);
        message_job = new GetMessagesJob(
                url, user, password, schema,
                times.getStart(), times.getEnd(),
                filters, max_properties)
        {
            @Override
            void gotMessages(final Message[] messages)
            {
                if (messages == null)
                    return;
                Model.this.messages = messages;
                fireModelChanged();
            }
        };
        message_job.schedule();
    }

    /** @return All model messages */
    public Message[] getMessages()
    {
        // Actually hands the original array out, no defensive copy.
        // Seems to be OK because TableViewer creates copy anyway
        // before filtering, sorting etc.
        return messages;
    }

    /** Send 'modelChanged' event to all listeners */
    @SuppressWarnings("nls")
    protected void fireModelChanged()
    {
        for (ModelListener listener : listeners)
        {
            try
            {
                listener.modelChanged(this);
            }
            catch (Throwable ex)
            {
                Activator.getLogger().log(Level.SEVERE, "Model update error", ex);
            }
        }
    }
}
