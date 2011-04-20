/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.Messages;
import org.csstudio.archive.common.engine.model.ArchiveChannel;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.BufferStats;
import org.csstudio.archive.common.engine.model.EngineModel;

/** Provide web page with basic info for all the groups.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class GroupsResponse extends AbstractResponse {
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    GroupsResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Groups");

        createGroupsTable(html);

        html.close();
    }

    private void openTableWithHeader(@Nonnull final HTMLWriter html) {
        html.openTable(1, new String[] {
                                        Messages.HTTP_COLUMN_GROUP,
                                        Messages.HTTP_COLUMN_CHANNEL_COUNT,
                                        Messages.HTTP_COLUMN_CONNECTED,
                                        Messages.HTTP_COLUMN_RECEIVEDVALUES,
                                        Messages.HTTP_COLUMN_QUEUEAVG,
                                        Messages.HTTP_COLUMN_QUEUEMAX,
        });
    }

    private void createGroupsTable(@Nonnull final HTMLWriter html) {

        openTableWithHeader(html);

        int totalNumOfChannels = 0;
        int totalNumOfConnectedChannels = 0;
        long totalNumOfReceivedSamples = 0;

        for (final ArchiveGroup group : _model.getGroups()) {
            int numOfConnectedChannels = 0;
            double avgQueueLength = 0;
            int maxQueueLength = 0;
            long numOfReceivedSamples = 0;

            final Collection<ArchiveChannel<?, ?>> channels = group.getChannels();
            for (final ArchiveChannel<?, ?> channel : channels) {
                if (channel.isConnected()) {
                    ++numOfConnectedChannels;
                }
                numOfReceivedSamples += channel.getReceivedValues();
                final BufferStats stats = channel.getSampleBuffer().getBufferStats();
                avgQueueLength += stats.getAverageSize();
                if (maxQueueLength < stats.getMaxSize()) {
                    maxQueueLength = stats.getMaxSize();
                }
            }
            final int numOfChannels = channels.size();
            if (numOfChannels > 0) {
                avgQueueLength /= numOfChannels;
            }
            totalNumOfChannels += numOfChannels;
            totalNumOfConnectedChannels += numOfConnectedChannels;
            totalNumOfReceivedSamples += numOfReceivedSamples;

            final String connected = numOfChannels == numOfConnectedChannels
                ? Integer.toString(numOfConnectedChannels)
                : HTMLWriter.makeRedText(Integer.toString(numOfConnectedChannels));

            html.tableLine(new String[] {
                HTMLWriter.makeLink("group?name=" + group.getName(), group.getName()),
                Integer.toString(numOfChannels),
                connected,
                Long.toString(numOfReceivedSamples),
                String.format("%.1f", avgQueueLength),
                Integer.toString(maxQueueLength),
            });
        }

        closeTableWithSummaryRow(html,
                                 totalNumOfChannels,
                                 totalNumOfConnectedChannels,
                                 totalNumOfReceivedSamples);
    }

    private void closeTableWithSummaryRow(@Nonnull final HTMLWriter html,
                                          final int totalNumOfChannels,
                                          final int totalNumOfConnectedChannels,
                                          final long totalNumOfReceivedSamples) {
        final String connected = totalNumOfChannels == totalNumOfConnectedChannels
        	? Integer.toString(totalNumOfConnectedChannels)
        	: HTMLWriter.makeRedText(Integer.toString(totalNumOfConnectedChannels));
        html.tableLine(new String[] {
            Messages.HTTP_ROW_TOTAL,
            Integer.toString(totalNumOfChannels),
            connected,
            Long.toString(totalNumOfReceivedSamples),
            "",
            "",
        });
        html.closeTable();
    }

}
