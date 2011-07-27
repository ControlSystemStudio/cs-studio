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
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE_H;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE_M;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.END;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.START;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveSampleDaoImpl}.
 *
 * @author bknerr
 * @since 27.07.2011
 */
public class ArchiveSampleDaoUnitTest extends AbstractDaoTestSetup {
    private static IArchiveSampleDao DAO;

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveSampleDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testCreateSamples() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        DAO.createSamples(TestSampleProvider.SAMPLES);

        Thread.sleep(2500);

        undoCreateSamples();
    }

    private static void undoCreateSamples() throws ArchiveConnectionException, SQLException {
        final Connection connection = HANDLER.getConnection();
        final Statement stmt = connection.createStatement();
        executeStatementForTable(stmt, TAB_SAMPLE);
        executeStatementForTable(stmt, TAB_SAMPLE_M);
        executeStatementForTable(stmt, TAB_SAMPLE_H);
        stmt.close();
    }

    private static void executeStatementForTable(@Nonnull final Statement stmt,
                                                 @Nonnull final String table) throws SQLException {
        stmt.execute("DELETE FROM " + table + " " +
                     "WHERE " + COLUMN_TIME + " between " + START.minusMillis(1L).getNanos() + " AND " + END.plusMillis(1L).getNanos() + " " +
                     "AND " + COLUMN_CHANNEL_ID + "=" + CHANNEL_ID.asString());
    }

    @AfterClass
    public static void undoBatchedStatements() throws ArchiveConnectionException, SQLException {
        undoCreateSamples();
    }

}
