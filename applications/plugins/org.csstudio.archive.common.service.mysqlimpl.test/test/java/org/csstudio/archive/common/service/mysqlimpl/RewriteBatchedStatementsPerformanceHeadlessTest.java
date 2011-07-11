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
import org.junit.Test;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 11.07.2011
 */
public class RewriteBatchedStatementsPerformanceHeadlessTest extends AbstractDaoTestSetup {

    /**
     * {@inheritDoc}
     */
    @Override
    public void before() throws ArchiveConnectionException, SQLException {
        super.before();

        final PreparedStatement stmt = HANDLER.getConnection().prepareStatement("CREATE TEMPORARY TABLE batchedTest (id INT(10) UNSIGNED NOT NULL, time BIGINT NOT NULL, value VARCHAR(32000) NOT NULL, PRIMARY KEY(id)) ENGINE=InnoDB AUTO_INCREMENT=1");
        stmt.execute();
        stmt.close();
    }

    @Test
    public void writeBatchedStatementsWithRewriteFlag() throws ArchiveConnectionException, SQLException {
        final PreparedStatement stmt = HANDLER.getConnection().prepareStatement("INSERT INTO batchedTest (id, time, value) VALUES (1, 100000000000, '(fi,fu,fa)')");
        stmt.execute();
        stmt.close();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void after() throws ArchiveConnectionException, SQLException {
//        final Connection con = HANDLER.getConnection();
//        final PreparedStatement stmt = con.prepareStatement("DROP TABLE IF EXISTS batchedTest");
//        stmt.execute();
//        stmt.close();

        super.after();
    }

}
