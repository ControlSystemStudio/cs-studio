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
package org.csstudio.archive.common.service.mysqlimpl.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Strategy for those statements that shall be batched.
 *
 * @author bknerr
 * @since 20.07.2011
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 */
public abstract class BatchQueueHandlerSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    private String _sqlStmtString;
    private final BlockingQueue<T> _queue;


    /**
     * Constructor.
     */
    public BatchQueueHandlerSupport(@Nonnull final Class<T> typeClass,
                                    @Nonnull final String sqlStmtString,
                                    @Nonnull final BlockingQueue<T> queue) {
        super(typeClass, BatchQueueHandlerSupport.class);

        _sqlStmtString = sqlStmtString;
        _queue = queue;
    }

    /**
     * Install a specific batch queue handler support if it does not already exist.
     *
     * @param <T>
     * @param handler
     */
    public static <T> void installHandlerIfNotExists(@Nonnull final BatchQueueHandlerSupport<T> handler) {
        final Class<T> type = handler.getType();

        AbstractTypeSupport.installIfNotExists(BatchQueueHandlerSupport.class, type, handler);
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    public static Collection<BatchQueueHandlerSupport> getInstalledHandlers() {
        final Collection<BatchQueueHandlerSupport> handlers = typeSupportsFor(BatchQueueHandlerSupport.class);
        if (handlers != null) {
            return handlers;
        }
        return Collections.emptyList();
    }

    public static <T> void addToQueue(@Nonnull final Collection<T> newEntries) throws TypeSupportException {
        if (newEntries.isEmpty()) {
            return;
        }
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) newEntries.iterator().next().getClass();
        final BatchQueueHandlerSupport<T> support = (BatchQueueHandlerSupport<T>) findTypeSupportForOrThrowTSE(BatchQueueHandlerSupport.class, type);

        final BlockingQueue<T> queue = support.getQueue();
        queue.addAll(newEntries);
    }

    @Nonnull
    public String getSqlStatementString() {
        return _sqlStmtString;
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
        final String sql = getSqlStatementString();
        return connection.prepareStatement(sql);
    }

    @Nonnull
    public BlockingQueue<T> getQueue() {
        return _queue;
    }

    @Nonnull
    public abstract Collection<String> convertToStatementString(@Nonnull final Collection<T> elements);

    @Nonnull
    public Class<T> getHandlerType() {
        return getType();
    }

    /**
     * For test purposes only, see calling method. This is a code smell, definitely, but unfortunately
     * it is not easily possible to intercept the life cycle mgmt of classes by the eclipse rcp framework.
     * For more details see the caller of this method.
     */
    public void setDatabase(@Nonnull final String databaseName) {
        _sqlStmtString = _sqlStmtString.replaceFirst("^INSERT IGNORE INTO [^\\.]*", "INSERT IGNORE INTO " + databaseName);
        _sqlStmtString = _sqlStmtString.replaceFirst("^UPDATE [^\\.]*", "UPDATE " + databaseName);

    }
}
