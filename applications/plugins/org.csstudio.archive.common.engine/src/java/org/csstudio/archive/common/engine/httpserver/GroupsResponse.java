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

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.SampleBufferStatistics;

/** Provide web page with basic info for all the groups.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class GroupsResponse extends AbstractResponse {

    private static final String URL_BASE_PAGE = "/groups";

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
        html.openTable(1, new String[] {Messages.HTTP_COLUMN_GROUP,
                                        numOf(Messages.HTTP_COLUMN_CHANNELS),
                                        numOf(Messages.HTTP_CONNECTED),
                                        numOf(Messages.HTTP_COLUMN_RECEIVEDVALUES),
                                        Messages.HTTP_COLUMN_QUEUEAVG,
                                        Messages.HTTP_COLUMN_QUEUEMAX,
                                        });
    }

    private void createGroupsTable(@Nonnull final HTMLWriter html) {

        openTableWithHeader(html);

        int totalNumOfChannels = 0;
        int totalNumOfConnectedChannels = 0;
        long totalNumOfReceivedSamples = 0;

        for (final ArchiveGroup group : getModel().getGroups()) {

            int numOfConnectedChannels = 0;
            double avgQueueLength = 0;
            int maxQueueLength = 0;
            long numOfReceivedSamples = 0;

            @SuppressWarnings("rawtypes")
            final Collection<ArchiveChannelBuffer> channels = group.getChannels();
            for (@SuppressWarnings("rawtypes") final ArchiveChannelBuffer channel : channels) {
                if (channel.isConnected()) {
                    ++numOfConnectedChannels;
                }
                numOfReceivedSamples += channel.getReceivedValues();
                final SampleBufferStatistics stats = channel.getSampleBuffer().getBufferStats();
                avgQueueLength += stats.getAverageSize();
                maxQueueLength = Math.max(maxQueueLength, stats.getMaxSize());
            }
            final int numOfChannels = channels.size();
            if (numOfChannels > 0) {
                avgQueueLength /= numOfChannels;
            }
            totalNumOfChannels += numOfChannels;
            totalNumOfConnectedChannels += numOfConnectedChannels;
            totalNumOfReceivedSamples += numOfReceivedSamples;

            html.tableLine(new String[] {
                                         ShowGroupResponse.linkTo(group.getName()),
                                         Integer.toString(numOfChannels),
                                         createChannelConnectedTableEntry(numOfConnectedChannels, numOfChannels),
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

    @Nonnull
    private String createChannelConnectedTableEntry(final int numOfConnectedChannels,
                                                    final int numOfChannels) {
        final String connected = numOfChannels == numOfConnectedChannels
            ? Integer.toString(numOfConnectedChannels)
            : HTMLWriter.makeRedText(Integer.toString(numOfConnectedChannels));
        return connected;
    }

    private void closeTableWithSummaryRow(@Nonnull final HTMLWriter html,
                                          final int totalNumOfChannels,
                                          final int totalNumOfConnectedChannels,
                                          final long totalNumOfReceivedSamples) {
        html.tableLine(new String[] {
            Messages.HTTP_ROW_TOTAL,
            Integer.toString(totalNumOfChannels),
            createChannelConnectedTableEntry(totalNumOfConnectedChannels, totalNumOfChannels),
            Long.toString(totalNumOfReceivedSamples),
            "",
            "",
        });
        html.closeTable();
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }

    @Nonnull
    public static String linkTo(@Nonnull final String linkText) {
        return new Url(baseUrl()).link(linkText);
    }
}
