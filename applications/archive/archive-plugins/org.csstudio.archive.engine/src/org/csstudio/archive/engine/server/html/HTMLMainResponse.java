/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.html;

import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.engine.server.AbstractMainResponse;
import org.csstudio.archive.vtype.TimestampHelper;
import org.diirt.util.time.TimeDuration;
import org.eclipse.core.runtime.Platform;

/** Provide web page with engine overview in HTML.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HTMLMainResponse extends AbstractMainResponse
{
    public HTMLMainResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MainTitle);
        html.openTable(2, new String[] { "Summary" });

        html.tableLine(new String[] { Messages.HTTP_Version, EngineModel.VERSION });

        html.tableLine(new String[] { Messages.HTTP_Description, model.getName() });

        html.tableLine(new String[] { Messages.HTTP_Host, host + ":" + req.getLocalPort() });

        html.tableLine(new String[] { Messages.HTTP_State, model.getState().name() });
        final Instant start = model.getStartTime();
        if (start != null)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_StartTime,
                TimestampHelper.format(start)
            });

            final double up_secs =
            TimeDuration.toSecondsDouble(Duration.between(start, Instant.now()));
            html.tableLine(new String[]
            {
                Messages.HTTP_Uptime,
                PeriodFormat.formatSeconds(up_secs)
            });
        }

        html.tableLine(new String[]
        {
            Messages.HTTP_Workspace,
            Platform.getInstanceLocation().getURL().getFile().toString()
        });

        updateChannelCount();

        html.tableLine(new String[]
        { Messages.HTTP_GroupCount, Integer.toString(model.getGroupCount()) });
        html.tableLine(new String[]
        { Messages.HTTP_ChannelCount, Integer.toString(totalChannelCount) });

        if (disconnectCount > 0)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_Disconnected,
                HTMLWriter.makeRedText(Integer.toString(disconnectCount))
            });
        }
        html.tableLine(new String[]
        {
            Messages.HTTP_BatchSize,
            model.getBatchSize() + " samples"
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WritePeriod,
            model.getWritePeriod() + " sec"
        });

        // Currently in 'Write Error' state?
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteState,
            (SampleBuffer.isInErrorState()
             ? HTMLWriter.makeRedText(Messages.HTTP_WriteError)
             : "OK")
        });

        final Instant last_write_time = model.getLastWriteTime();
        html.tableLine(new String[]
        {
          Messages.HTTP_LastWriteTime,
          (last_write_time == null
          ? Messages.HTTP_Never
          : TimestampHelper.format(last_write_time))
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteCount,
            (int)model.getWriteCount() + " samples"
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteDuration,
            String.format("%.1f sec", model.getWriteDuration())
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_Idletime,
            String.format("%.1f %%", model.getIdlePercentage())
        });

        final Runtime runtime = Runtime.getRuntime();
        final double used_mem = runtime.totalMemory() / MB;
        final double max_mem = runtime.maxMemory() / MB;
        final double perc_mem = max_mem > 0 ?
                     used_mem / max_mem * 100.0 : 0.0;
        html.tableLine(new String[]
        {
            "Memory",
            String.format("%.1f MB of %.1f MB used (%.1f %%)",
                          used_mem, max_mem, perc_mem)
        });

        html.closeTable();

        html.close();
    }
}
