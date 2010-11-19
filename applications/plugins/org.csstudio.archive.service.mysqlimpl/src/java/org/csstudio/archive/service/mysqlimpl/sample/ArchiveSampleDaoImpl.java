/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.service.mysqlimpl.sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.mysqlimpl.MySQLArchiveServicePreference;
import org.csstudio.archive.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.logging.CentralLogger;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Archive sample dao implementation.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public class ArchiveSampleDaoImpl extends AbstractArchiveDao implements IArchiveSampleDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveSampleDaoImpl.class);

    private static final String RETRIEVAL_ARCHIVE = "Channel configuration retrieval from archive failed.";

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectLastSmplTimeByChannelIdStmt =
        "SELECT MAX(smpl_time) FROM archive.sample WHERE channel_id=?";

    private final String _insertSamplesStmt = "INSERT INTO archive.sample (channel_id, smpl_time, severity_id, status_id, float_val, nanosecs) VALUES ";

    private static final DateTimeFormatter SAMPLE_TIME_FMT = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public TimeInstant retrieveLatestSampleByChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveSampleDaoException {

        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectLastSmplTimeByChannelIdStmt);
            stmt.setInt(1, id.intValue());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {

                final Timestamp ltstSmplTime = result.getTimestamp(1);
                return TimeInstant.fromMillis(ltstSmplTime.getTime());
            }

        } catch (final ArchiveConnectionException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_ARCHIVE, e);
        } catch (final SQLException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_ARCHIVE, e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectLastSmplTimeByChannelIdStmt + " failed.");
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createSamples(@Nonnull final Collection<IArchiveSample<?>> samples) {

        final StringBuilder values = new StringBuilder();
        values.append(_insertSamplesStmt);
        for (final IArchiveSample<?> sample : samples) {
            final EpicsAlarm alarm = sample.getAlarm();

            final int sevId = DAO_MGR.getSeverityDao().retrieveSeverityId(alarm.getSeverity());
            final int statusId = DAO_MGR.getStatusDao().retrieveStatusId(alarm.getStatus());

            values.append("(" +
                          sample.getChannelId().intValue() + "," +
                          SAMPLE_TIME_FMT.print((ReadableInstant) sample.getTimestamp()) + "," +
                          sevId + "," +
                          statusId + "," +
                          sample.getValue().toString() + "," +
                          sample.getTimestamp().getFractalSecondInNanos() +
                          "),");
        }
        values.delete(values.length() - 1, values.length()); // deletes last comma

        // TODO (bknerr) : LOG warning if single statement size is too large.
        // and when com.mysql.jdbc.PacketTooBigException:
        // Packet for query is too large (45804672 > 1048576).
        // You can change this value on the server by setting the max_allowed_packet' variable.
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(values.toString());
            stmt.setQueryTimeout(MySQLArchiveServicePreference.SQL_TIMEOUT.getValue());
            stmt.execute();
            getConnection().commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

    }

}
