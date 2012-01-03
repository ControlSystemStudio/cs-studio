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
package org.csstudio.archive.common.service.mysqlimpl.channel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.channel.UpdateDisplayInfoBatchQueueHandler.ArchiveChannelDisplayInfo;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * DBO specific batch strategy for high write throughput.
 *
 * @author bknerr
 * @since 20.07.2011
 */
public class UpdateDisplayInfoBatchQueueHandler extends BatchQueueHandlerSupport<ArchiveChannelDisplayInfo> {

    /**
     * Entity holding the update display range information.
     *
     * @author bknerr
     * @since 20.07.2011
     */
    public static final class ArchiveChannelDisplayInfo {
        private final ArchiveChannelId _id;
        private final String _high;
        private final String _low;

        /**
         * Constructor.
         */
        public ArchiveChannelDisplayInfo(@Nonnull final ArchiveChannelId id,
                                         @Nonnull final String high,
                                         @Nonnull final String low) {
            _id = id;
            _high = high;
            _low = low;
        }
        @Nonnull
        public String getHigh() {
            return _high;
        }
        @Nonnull
        public String getLow() {
            return _low;
        }
        @Nonnull
        public ArchiveChannelId getId() {
            return _id;
        }
    }

    /**
     * Constructor.
     */
    public UpdateDisplayInfoBatchQueueHandler(@Nonnull final String databaseName) {
        super(ArchiveChannelDisplayInfo.class,
              createSqlStatementString(databaseName),
              new LinkedBlockingQueue<ArchiveChannelDisplayInfo>());
    }

    @Nonnull
    private static String createSqlStatementString(@Nonnull final String database) {
        return "UPDATE " + database + "." + ArchiveChannelDaoImpl.TAB +
               " SET display_high=?, display_low=? WHERE id=?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillStatement(@Nonnull final PreparedStatement stmt,
                              @Nonnull final ArchiveChannelDisplayInfo element) throws SQLException {
        stmt.setString(1, element.getHigh());
        stmt.setString(2, element.getLow());
        stmt.setInt(3, element.getId().intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final Collection<ArchiveChannelDisplayInfo> elements) {
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        final String sqlStr = getSqlStatementString();
        final Collection<String> statements =
            Collections2.transform(elements,
                                   new Function<ArchiveChannelDisplayInfo, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final ArchiveChannelDisplayInfo input) {
                                           String result;
                                           result = sqlStr.replaceFirst("\\?", "'" + input.getHigh() + "'");
                                           result = result.replaceFirst("\\?", "'" + input.getLow() + "'");
                                           result = result.replaceFirst("\\?", input.getId().asString());
                                           return result + ";";
                                       }
                                    });
        return statements;
    }
}
