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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.sample.IArchiveSample;

/**
 * Test handler for provoking an SQL exception on filling the statement.
 *
 * @author bknerr
 * @since 02.08.2011
 */
@SuppressWarnings("rawtypes")
final class FalseFillStmtHandler extends
        BatchQueueHandlerSupport<IArchiveSample> {

    private int _i = -1;
    /**
     * Constructor.
     */
    FalseFillStmtHandler(@Nonnull final Class<IArchiveSample> typeClass,
                         @Nonnull final String database,
                         @Nonnull final Queue<IArchiveSample> queue) {
        super(typeClass, createSqlStatementString(database), queue);
    }

    @Override
    protected void fillStatement(@Nonnull final PreparedStatement stmt,
                                 @Nonnull final IArchiveSample element)
    throws ArchiveDaoException, SQLException {
        stmt.setInt(-1, -1); // wrong statement
    }

    @Nonnull
    private static String createSqlStatementString(@SuppressWarnings("unused") @Nonnull final String database) {
        return PersistDataWorkerHeadlessTest.TEST_STATEMENT;
    }

    @Override
    @Nonnull
    public Collection<String> convertToStatementString(@Nonnull final Collection<IArchiveSample> elements) {
        _i++;
        return Collections.singleton(getSqlStatementString() + _i + ";");
    }
}
