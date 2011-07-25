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
package org.csstudio.archive.common.service.mysqlimpl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for write performance of a single statement with a long VALUES list of inserts vs a single
 * 'batched' statement with option rewritebatchedstatement=true.
 *
 * @author bknerr
 * @since 11.07.2011
 */
public class RewriteBatchedStatementsPerformanceUnitTest extends AbstractDaoTestSetup {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeHook() throws ArchiveConnectionException, SQLException {
        super.beforeHook();

        final PreparedStatement stmt =
            HANDLER.getConnection().prepareStatement("CREATE TEMPORARY TABLE IF NOT EXISTS batchedTest (id INT(10) UNSIGNED NOT NULL, time BIGINT NOT NULL, value VARCHAR(32000) NOT NULL, PRIMARY KEY(id)) ENGINE=InnoDB AUTO_INCREMENT=1");
        stmt.execute();
        stmt.close();
    }

    @Test
    public void singleStmtStrVsBatchedStmt() throws ArchiveConnectionException, SQLException {
        final long numOfInserts = 10000;
        final long elapsedStr = writeSingleLargeStmtWithValuesList(numOfInserts);

        final long elapsedBatch = writeBatchStatement(numOfInserts);

        Assert.assertTrue(elapsedStr > elapsedBatch);
    }

    public long writeSingleLargeStmtWithValuesList(final long numOfInserts) throws ArchiveConnectionException, SQLException {
        final RunningStopWatch watch = StopWatch.start();

        final StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO batchedTest (id, time, value) VALUES (0, 100000000000, '(fi,fu,fa)')");
        for (int i = 1; i < numOfInserts; i++) {
            builder.append(",").append("(").append(i).append(", 100000000000, '(fi,fu,fa)')");
        }
        final long strTime = watch.getElapsedTimeInNS();
        //System.out.println("string assembly : " + strTime);

        watch.restart();
        final PreparedStatement stmt =
            HANDLER.getConnection().prepareStatement(builder.toString());

        stmt.execute();
        stmt.close();

        final long execTime = watch.getElapsedTimeInNS();
        //System.out.println("stmt exec : " + execTime);

        return strTime + execTime;
    }

    public long writeBatchStatement(final long numOfInserts) throws ArchiveConnectionException, SQLException {
        final RunningStopWatch watch = StopWatch.start();

        final PreparedStatement stmt =
            HANDLER.getConnection().prepareStatement("INSERT INTO batchedTest (id, time, value) VALUES (?, ?, ?)");

        for (int i = (int) numOfInserts; i < 2*numOfInserts; i++) {
            stmt.setInt(1, i);
            stmt.setLong(2, 100000000000L);
            stmt.setString(3, "(fi,fu,fa)");
            stmt.addBatch();
        }
        final long batchTime = watch.getElapsedTimeInNS();
        //System.out.println("batch assembly : " + batchTime);

        watch.restart();

        stmt.executeBatch();
        stmt.close();

        final long execTime = watch.getElapsedTimeInNS();
        //System.out.println("stmt exec : " + execTime);

        return execTime + batchTime;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterHook() throws ArchiveConnectionException, SQLException {
        final PreparedStatement stmt =
            HANDLER.getConnection().prepareStatement("DROP TEMPORARY TABLE IF EXISTS batchedTest");
        stmt.execute();
        stmt.close();

        super.afterHook();
    }

}
