/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.service.mysqlimpl.channelstatus;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * DBO specific batch strategy for high write throughput.
 *
 * @author bknerr
 * @since 20.07.2011
 */
public class ArchiveChannelStatusBatchQueueHandler extends BatchQueueHandlerSupport<IArchiveChannelStatus> {
    private static final String VAL_WILDCARDS = "(?, ?, ?, ?)";

    /**
     * Constructor.
     */
    public ArchiveChannelStatusBatchQueueHandler(@Nonnull final String databaseName) {
        super(IArchiveChannelStatus.class,
              createSqlStatementString(databaseName),
              new LinkedBlockingQueue<IArchiveChannelStatus>());
    }

    @Nonnull
    private static String createSqlStatementString(@Nonnull final String database) {
        return "INSERT IGNORE INTO " + database + "." + ArchiveChannelStatusDaoImpl.TAB +
               " (channel_id, connected, info, time) VALUES " + VAL_WILDCARDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillStatement(@Nonnull final PreparedStatement stmt,
                              @Nonnull final IArchiveChannelStatus element) throws SQLException {
        stmt.setInt(1, element.getChannelId().intValue());
        stmt.setString(2, (element.isConnected() ? "TRUE" : "FALSE"));
        stmt.setString(3, element.getInfo());
        stmt.setLong(4, element.getTime().getNanos());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final Collection<IArchiveChannelStatus> elements) {
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        final String sqlWithoutValues = getSqlStatementString().replace(VAL_WILDCARDS, "");

        final Collection<String> valueList =
            Collections2.transform(elements,
                                   new Function<IArchiveChannelStatus, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final IArchiveChannelStatus entry) {
                                           final String result =
                                               "(" +
                                               Joiner.on(",").join(entry.getChannelId().intValue(),
                                                                   entry.isConnected() ? "'TRUE'" : "'FALSE'",
                                                                   "'" + entry.getInfo() + "'",
                                                                   entry.getTime().getNanos()) +
                                               ")";
                                           return result;
                                       }
                                    });
        return Collections.singleton(sqlWithoutValues + Joiner.on(",").join(valueList) + ";");
    }
}
