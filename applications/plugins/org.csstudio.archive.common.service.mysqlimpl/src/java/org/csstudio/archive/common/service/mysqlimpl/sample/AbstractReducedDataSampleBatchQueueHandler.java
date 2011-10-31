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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_AVG;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_MAX;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_MIN;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_TIME;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Batch queue handler for reduced data samples.
 *
 * @author bknerr
 * @since 20.07.2011
 * @param <T> the type of the entity used to fill the statement's batch
 */
public abstract class AbstractReducedDataSampleBatchQueueHandler<T extends AbstractReducedDataSample> extends BatchQueueHandlerSupport<T> {
    protected static final String VALUES_WILDCARD = "(?, ?, ?, ?, ?)";

    /**
     * Constructor.
     */
    public AbstractReducedDataSampleBatchQueueHandler(@Nonnull final Class<T> typeClass,
                                                      @Nonnull final String sqlStmtString,
                                                      @Nonnull final BlockingQueue<T> queue) {
        super(typeClass, sqlStmtString, queue);
    }

    @Nonnull
    protected static String createSqlStatementString(@Nonnull final String database,
                                                     @Nonnull final String table) {
        final String sql =
            "INSERT IGNORE INTO " + database + "." + table +
            " (" + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_AVG, COLUMN_MIN, COLUMN_MAX) +
            ") VALUES " + VALUES_WILDCARD;
        return sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillStatement(@Nonnull final PreparedStatement stmt,
                                 @Nonnull final T element)
                                 throws ArchiveDaoException,
                                        SQLException {
        stmt.setInt(1, element.getChannelId().intValue());
        stmt.setLong(2, element.getTimestamp().getNanos());

        stmt.setDouble(3, element.getAvg());
        stmt.setDouble(4, element.getMin());
        stmt.setDouble(5, element.getMax());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final Collection<T> elements) {
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        final String sqlWithoutValues = getSqlStatementString().replace(VALUES_WILDCARD, "");

        final Collection<String> values =
            Collections2.transform(elements,
                                   new Function<AbstractReducedDataSample, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final AbstractReducedDataSample input) {
                                           final String result =
                                               "(" +
                                               Joiner.on(",").join(input.getChannelId().asString(),
                                                                   input.getTimestamp().getNanos(),
                                                                   input.getAvg(),
                                                                   input.getMin(),
                                                                   input.getMax()) +
                                               ")";
                                           return result;
                                       }
                                    });
        return Collections.singleton(sqlWithoutValues + Joiner.on(",").join(values) + ";");
    }


}
