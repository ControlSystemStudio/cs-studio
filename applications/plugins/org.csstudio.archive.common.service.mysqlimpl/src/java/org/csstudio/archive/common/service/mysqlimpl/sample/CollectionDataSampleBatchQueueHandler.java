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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.common.codec.BaseCodecUtil;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * {@link BatchQueueHandlerSupport} for samples for serializable collections.
 *
 * @author bknerr
 * @since 10.08.2011
 */
@SuppressWarnings("rawtypes")
public class CollectionDataSampleBatchQueueHandler extends BatchQueueHandlerSupport<ArchiveMultiScalarSample> {

    public static final String TAB_SAMPLE_BLOB = "sample_blob";

    static final Logger LOG = LoggerFactory.getLogger(CollectionDataSampleBatchQueueHandler.class);

    private static final String VALUES_WILDCARD = "(?, ?, ?)";

    /**
     * Constructor.
     */
    public CollectionDataSampleBatchQueueHandler(@Nonnull final String databaseName) {
        super(ArchiveMultiScalarSample.class,
              createSqlStatementString(databaseName),
              new LinkedBlockingQueue<ArchiveMultiScalarSample>());
    }

    @Nonnull
    private static String createSqlStatementString(@Nonnull final String database) {
        final String sql =
            "INSERT IGNORE INTO " + database + "." + TAB_SAMPLE_BLOB + " " +
            "(" + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_VALUE)+ ") " +
            "VALUES " + VALUES_WILDCARD;
        return sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillStatement(@Nonnull final PreparedStatement stmt,
                                 @Nonnull final ArchiveMultiScalarSample element) throws ArchiveDaoException,
                                                                                         SQLException {
        stmt.setInt(1, element.getChannelId().intValue());
        stmt.setLong(2, element.getSystemVariable().getTimestamp().getNanos());

        try {
            final byte[] byteArray = ArchiveTypeConversionSupport.toByteArray(element.getValue());
            stmt.setBytes(3, byteArray);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Archive type support for byte array conversion failed for " +
                                          element.getValue().getClass().getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final Collection<ArchiveMultiScalarSample> elements) {
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        final String sqlWithoutValues = getSqlStatementString().replace(VALUES_WILDCARD, "");

        final Collection<String> sqlStatementStrings =
            Collections2.transform(elements,
                                   new Function<ArchiveMultiScalarSample, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final ArchiveMultiScalarSample input) {
                                           try {
                                               final byte[] byteArray = ArchiveTypeConversionSupport.toByteArray(input.getValue());
                                               final String hexStr = BaseCodecUtil.getHex(byteArray);

                                               final String value = sqlWithoutValues +
                                                   "(" +
                                                   Joiner.on(",").join(input.getChannelId().asString(),
                                                                       input.getSystemVariable().getTimestamp().getNanos(),
                                                                       "x'" + hexStr + "'") +
                                                   ");";
                                               return value;
                                           } catch (final TypeSupportException e) {
                                               LOG.error("Type support missing for " + input.getValue().getClass().getName(), e);
                                           }
                                           return "";
                                       }
                                    });
        return sqlStatementStrings;
    }

}
