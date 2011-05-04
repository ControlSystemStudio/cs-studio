/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.Messages;
import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.runtime.Platform;
import org.joda.time.Duration;

/** Provide web page with engine overview.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class MainResponse extends AbstractResponse {

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    /** Bytes in a MegaByte */
    final static double MB = 1024.0*1024.0;

    private static String host = null;

    MainResponse(final EngineModel model)
    {
        super(model);

        if (host == null)
        {
            try
            {
                final InetAddress localhost = InetAddress.getLocalHost();
                host = localhost.getHostName();
            }
            catch (final Exception ex)
            {
                host = "localhost";
            }
        }
    }

    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MainTitle);
        html.openTable(2, new String[] { "Summary" });

        html.tableLine(new String[] { Messages.HTTP_Version, EngineModel.VERSION });

        html.tableLine(new String[] { Messages.HTTP_Description, getModel().getName() });

        html.tableLine(new String[] { Messages.HTTP_Host, host + ":" + req.getLocalPort() });

        html.tableLine(new String[] { Messages.HTTP_State, getModel().getState().name() });
        final TimeInstant start = getModel().getStartTime();
        if (start != null)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_StartTime,
                start.formatted()
            });

            final Duration dur = new Duration(start.getInstant(),
                                              TimeInstantBuilder.fromNow().getInstant());

            html.tableLine(new String[]
            {
                Messages.HTTP_Uptime,
                TimeInstant.STD_DURATION_FMT.print(dur.toPeriod())
            });
        }

        html.tableLine(new String[]
        {
            Messages.HTTP_Workspace,
            Platform.getInstanceLocation().getURL().getFile().toString()
        });

        final int group_count = getModel().getGroups().size();
        int total_channel_count = 0;
        int connect_count = 0;
        for (final ArchiveGroup group : getModel().getGroups()) {

            final int channel_count = group.getChannels().size();
            for (final ArchiveChannel<?, ?> channel : group.getChannels()) {

                if (channel.isConnected()) {
                    ++connect_count;
                }
            }
            total_channel_count += channel_count;
        }
        html.tableLine(new String[]
        { Messages.HTTP_GroupCount, Integer.toString(group_count) });
        html.tableLine(new String[]
        { Messages.HTTP_COLUMN_CHANNEL_COUNT, Integer.toString(total_channel_count) });
        final int disconnect_count = total_channel_count - connect_count;
        if (disconnect_count > 0)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_Disconnected,
                HTMLWriter.makeRedText(Integer.toString(disconnect_count))
            });
        }
        html.tableLine(new String[]
        {
            Messages.HTTP_WritePeriod,
            getModel().getWritePeriodInMS() + " ms"
        });

        // Currently in 'Write Error' state?
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteState,
            (SampleBuffer.isInErrorState()
             ? HTMLWriter.makeRedText(Messages.HTTP_WriteError)
             : "OK")
        });

        final TimeInstant lastWriteTime = getModel().getLastWriteTime();
        html.tableLine(new String[]
        {
            Messages.HTTP_LAST_WRITETIME,
           (lastWriteTime == null ? Messages.HTTP_Never :
                                    lastWriteTime.formatted())
        });
        final Double avgWriteCount = getModel().getAvgWriteCount();
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteCount,
            (avgWriteCount != null ? String.format("%.1f", avgWriteCount):
                                     "NO") + " samples"
        });
        final Duration avgWriteDuration = getModel().getAvgWriteDuration();
        String printDur = "NONE";
        if (avgWriteDuration != null) {
            printDur = TimeInstant.STD_DURATION_FMT.print(avgWriteDuration.toPeriod());
            if (StringUtil.isBlank(printDur)) {
                printDur = "<1s";
            }
        }
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteDuration,
            printDur
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
