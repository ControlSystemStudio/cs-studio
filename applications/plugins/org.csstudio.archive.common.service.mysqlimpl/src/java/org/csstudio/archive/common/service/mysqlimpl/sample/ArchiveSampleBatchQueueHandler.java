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

import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_TIME;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_VALUE;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * DBO specific batch strategy for high write throughput.
 *
 * @author bknerr
 * @since 20.07.2011
 */
@SuppressWarnings("rawtypes")
public class ArchiveSampleBatchQueueHandler extends BatchQueueHandlerSupport<IArchiveSample> {

    static final Logger LOG = LoggerFactory.getLogger(ArchiveSampleBatchQueueHandler.class);

    private static final String VAL_WILDCARDS = "(?, ?, ?)";

    /**
     * Constructor.
     */
    public ArchiveSampleBatchQueueHandler(@Nonnull final String databaseName) {
        super(IArchiveSample.class, databaseName, new LinkedBlockingQueue<IArchiveSample>());
    }

    @Override
    @Nonnull
    protected String composeSqlString() {
        final String sql =
            "INSERT INTO " + getDatabase() + "." + TAB_SAMPLE + " " +
            "(" + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_VALUE)+ ") " +
            "VALUES " + VAL_WILDCARDS;
        return sql;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public void fillStatement(@Nonnull final PreparedStatement stmt,
                              @Nonnull final IArchiveSample type) throws SQLException, ArchiveDaoException {
        stmt.setInt(1, type.getChannelId().intValue());
        stmt.setLong(2, type.getSystemVariable().getTimestamp().getNanos());
        try {
            stmt.setString(3, ArchiveTypeConversionSupport.toArchiveString(type.getValue()));
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("No type support found for " + type.getValue().getClass().getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final List<IArchiveSample> elements) {
        final String sqlWithoutValues = composeSqlString().replace(VAL_WILDCARDS, "");

        final Collection<String> values =
            Collections2.transform(elements,
                                   new Function<IArchiveSample, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final IArchiveSample input) {
                                           try {
                                               final String value =
                                                   "(" +
                                                   Joiner.on(",").join(input.getChannelId().asString(),+
                                                                       input.getSystemVariable().getTimestamp().getNanos(),
                                                                       "'" + ArchiveTypeConversionSupport.toArchiveString(input.getValue()) + "'") +
                                                   ")";
                                               return value;
                                           } catch (final TypeSupportException e) {
                                               LOG.error("Type support missing for " + input.getValue().getClass().getName(), e);
                                           }
                                           return null;
                                       }
                                    });
        return Collections.singleton(sqlWithoutValues + Joiner.on(",").join(values) + ";");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Class<IArchiveSample> getType() {
        return IArchiveSample.class;
    }

}
