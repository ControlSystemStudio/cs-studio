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
package org.csstudio.archive.common.service.mysqlimpl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnull;

/**
 * Strategy for those statements that shall be batched.
 *
 * @author bknerr
 * @since 20.07.2011
 * @param <T> the type of the entity used to fill the statement's batch
 */
public abstract class AbstractBatchQueueHandler<T> {

    private final String _database;
    private final BlockingQueue<T> _queue;

    /**
     * Constructor.
     */
    public AbstractBatchQueueHandler(@Nonnull final String database,
                             @Nonnull final BlockingQueue<T> queue) {
        _database = database;
        _queue = queue;
    }

    @Nonnull
    protected String getDatabase() {
        return _database;
    }

    public void applyBatch(@Nonnull final PreparedStatement stmt, @Nonnull final T element) throws ArchiveDaoException {
        try {
            stmt.clearParameters();
            fillStatement(stmt, element);
            stmt.addBatch();
        } catch (final SQLException e) {
            throw new ArchiveDaoException("Filling or adding of batch to prepared statement failed for " + element.toString(), e);
        }
    }
    protected abstract void fillStatement(@Nonnull final PreparedStatement stmt,
                                          @Nonnull final T element) throws ArchiveDaoException, SQLException;

    @Nonnull
    public PreparedStatement createNewStatement(@Nonnull final Connection connection) throws SQLException {
        final String sql = composeSqlString();
        return connection.prepareStatement(sql);
    }
    @Nonnull
    protected abstract String composeSqlString();

    @Nonnull
    public BlockingQueue<T> getQueue() {
        return _queue;
    }

    @Nonnull
    public abstract Collection<String> convertToStatementString(@Nonnull final List<T> elements);

    @Nonnull
    public abstract Class<T> getType();
}
