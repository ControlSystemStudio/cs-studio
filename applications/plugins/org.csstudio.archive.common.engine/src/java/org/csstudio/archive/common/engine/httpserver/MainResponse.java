/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBuffer;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.eclipse.core.runtime.Platform;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Provide web page with engine overview.
 *  @author Kay Kasemir
 */
class MainResponse extends AbstractResponse {

    private static final Logger LOG = LoggerFactory.getLogger(MainResponse.class);

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    /** Bytes in a MegaByte */
    private static final double MB = 1024.0*1024.0;

    private static String HOST = null;

    MainResponse(@Nonnull final EngineModel model) {
        super(model);

        if (HOST == null) {
            try {
                final InetAddress localhost = InetAddress.getLocalHost();
                HOST = localhost.getHostName();
            } catch (final UnknownHostException ex) {
                LOG.warn("Host IP address unknown for localhost, fall back to 'localhost' as host identifier.");
                HOST = "localhost";
            }
        }
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MAIN_TITLE);
        html.openTable(2, new String[] {Messages.HTTP_SUMMARY});

        createTableRows(req, html);

        html.closeTable();
        html.close();
    }

    private void createTableRows(@Nonnull final HttpServletRequest req,
                                 @Nonnull final HTMLWriter html) {
        createProgramInfoRows(req, html);

        createChannelStatsRows(html);

        createWriteStatsRows(html);

        createMemoryStatsRow(html);
    }

    private void createProgramInfoRows(@Nonnull final HttpServletRequest req,
                                       @Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {Messages.HTTP_VERSION, EngineModel.getVersion()});
        html.tableLine(new String[] {Messages.HTTP_DESCRIPTION, getModel().getName()});
        html.tableLine(new String[] {Messages.HTTP_HOST, HOST + ":" + req.getLocalPort()});
        html.tableLine(new String[] {Messages.HTTP_STATE, getModel().getState().name()});

        final TimeInstant start = getModel().getStartTime();
        if (start != null) {
            html.tableLine(new String[] {Messages.HTTP_STARTTIME, start.formatted()});
            final Duration dur = new Duration(start.getInstant(),
                                              TimeInstantBuilder.fromNow().getInstant());
            html.tableLine(new String[] {Messages.HTTP_UPTIME,
                                         TimeInstant.STD_DURATION_FMT.print(dur.toPeriod())});
        }

        html.tableLine(new String[] {Messages.HTTP_WORKSPACE,
                                     Platform.getInstanceLocation().getURL().getFile().toString()});
    }

    private void createChannelStatsRows(@Nonnull final HTMLWriter html) {
        int numOfChannels = 0;
        int numOfConnectedChannels = 0;
        for (final ArchiveGroup group : getModel().getGroups()) {
            numOfChannels += group.getChannels().size();

            for (final ArchiveChannel<?, ?> channel : group.getChannels()) {
                numOfConnectedChannels += channel.isConnected() ? 1 : 0;
            }
        }
        html.tableLine(new String[] {Messages.HTTP_COLUMN_GROUPCOUNT,
                                     String.valueOf(getModel().getGroups().size())});
        html.tableLine(new String[] {Messages.HTTP_COLUMN_CHANNEL_COUNT,
                                     String.valueOf(numOfChannels)});
        final int numOfDisconnectedChannels = numOfChannels - numOfConnectedChannels;
        if (numOfDisconnectedChannels > 0) {
            html.tableLine(new String[] {Messages.HTTP_NO,
                                         HTMLWriter.makeRedText(String.valueOf(numOfDisconnectedChannels))});
        }
    }

    private void createWriteStatsRows(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {Messages.HTTP_WRITE_PERIOD,
                                     getModel().getWritePeriodInMS() + " ms"});

        html.tableLine(new String[] {Messages.HTTP_WRITE_STATE,
                                     (SampleBuffer.isInErrorState() ? HTMLWriter.makeRedText(Messages.HTTP_WRITE_ERROR) :
                                                                      Messages.HTTP_NO_ERROR)});

        final TimeInstant lastWriteTime = getModel().getLastWriteTime();
        html.tableLine(new String[] {Messages.HTTP_LAST_WRITETIME,
                                     (lastWriteTime == null ? Messages.HTTP_NEVER :
                                                              lastWriteTime.formatted())});

        final Double avgWriteCount = getModel().getAvgWriteCount();
        html.tableLine(new String[] {Messages.HTTP_WRITE_COUNT,
                                     (avgWriteCount != null ? String.format("%.1f", avgWriteCount):
                                                              "NO") + " samples"});
        final Duration avgWriteDuration = getModel().getAvgWriteDuration();
        String printDur = "NONE";
        if (avgWriteDuration != null) {
            printDur =
                TimeInstant.STD_DURATION_WITH_MILLIES_FMT.print(avgWriteDuration.toPeriod());
            if (Strings.isNullOrEmpty(printDur)) {
                printDur = "<1";
            }
            printDur += "ms";
        }
        html.tableLine(new String[] {Messages.HTTP_WRITE_DURATION, printDur});
    }

    private void createMemoryStatsRow(@Nonnull final HTMLWriter html) {
        final Runtime runtime = Runtime.getRuntime();
        final double usedMem = runtime.totalMemory() / MB;
        final double maxMem = runtime.maxMemory() / MB;
        final double percMem = maxMem > 0 ? usedMem / maxMem * 100.0 : 0.0;
        html.tableLine(new String[] {"Memory",
                                     String.format("%.1f MB of %.1f MB used (%.1f %%)",
                                                   usedMem, maxMem, percMem)});
    }
}
