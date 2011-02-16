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
package org.csstudio.archive.common.service.mysqlimpl.dao;

import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.DATABASE_NAME;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.EMAIL_ADDRESS;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.FAILOVER_HOST;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.HOST;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.MAX_ALLOWED_PACKET;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.PASSWORD;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.PORT;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.SMTP_HOST;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.USER;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference;
import org.csstudio.archive.common.service.mysqlimpl.archivermgmt.ArchiverMgmtDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.archivermgmt.IArchiverMgmtDao;
import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.ArchiveChannelGroupDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.engine.ArchiveEngineDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;
import org.csstudio.archive.common.service.mysqlimpl.samplemode.ArchiveSampleModeDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.samplemode.IArchiveSampleModeDao;
import org.csstudio.archive.common.service.mysqlimpl.severity.ArchiveSeverityDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.severity.IArchiveSeverityDao;
import org.csstudio.archive.service.common.mysqlimpl.status.ArchiveStatusDaoImpl;
import org.csstudio.archive.service.common.mysqlimpl.status.IArchiveStatusDao;
import org.csstudio.email.EMailSender;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;

import com.google.common.collect.Sets;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * The archive dao manager.
 *
 * Envisioned to handle connection pools and transactions with CRUD command abstraction.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public enum ArchiveDaoManager implements IArchiveDaoManager {

    INSTANCE;

////    public interface IDaoCommand {
////        @CheckForNull
////        Object execute(@Nonnull final IDaoManager daoManager) throws ArchiveDaoException;
////    }
//    public interface IArchiveDaoCommand {
//        @CheckForNull
//        Object execute(IArchiveDaoManager daoManager) throws ArchiveDaoException;
//    }
//
//    @Override
//    public Object execute(final IArchiveDaoCommand command) throws ArchiveDaoException {
//            return command.execute(this);
//    }
//
//    @Override
//    public Object executeAndClose(final IArchiveDaoCommand command) throws ArchiveDaoException {
//        try{
//            return command.execute(this);
//        } finally {
//            try {
//                getConnection().close();
//            } catch (final ArchiveConnectionException e) {
//                throw new ArchiveDaoException("Connection could not be established.", e);
//            } catch (final SQLException e) {
//                throw new ArchiveDaoException("Connection could not be closed.", e);
//            }
//        }
//    }
//    @Override
//    public Object transaction(final IArchiveDaoCommand command) throws ArchiveDaoException {
//        Connection connection = null;
//        try {
//            try {
//                connection = getConnection();
//                connection.setAutoCommit(false);
//                final Object returnValue = command.execute(this);
//                connection.commit();
//                return returnValue;
//            } catch (final Exception e) {
//                if (connection != null) {
//                    LOG.warn("DAO command execution failed. Rollback.", e);
//                    connection.rollback();
//                }
//                throw new ArchiveDaoException("DAO command failed but has been rollbacked", e);
//            } finally {
//                if (connection != null) {
//                    connection.setAutoCommit(true);
//                }
//            }
//        } catch (final SQLException e1) {
//            throw new ArchiveDaoException("DAO command and rollback failed", e1);
//        }
//    }
//    @CheckForNull
//    public Object transactionAndClose(@Nonnull final IArchiveDaoCommand command) throws ArchiveDaoException {
//        return executeAndClose(new IArchiveDaoCommand(){
//            @Override
//            @CheckForNull
//            public Object execute(@Nonnull final IArchiveDaoManager manager) throws ArchiveDaoException {
//                return manager.transaction(command);
//            }
//        });
//    }

    private static final int MIN_PERIOD_MS = 3000;
    private static final int MAX_PERIOD_MS = 60000;
    private static final int DEFAULT_PERIOD_MS = 5000;
    private static final int KBYTE_SIZE = 1024;

    private static final String ARCHIVE_CONNECTION_EXCEPTION_MSG = "Archive connection could not be established";

    static final Logger LOG = CentralLogger.getInstance().getLogger(ArchiveDaoManager.class);
    static final Logger WORKER_LOG = CentralLogger.getInstance().getLogger(PersistDataWorker.class);


    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    private final ScheduledThreadPoolExecutor _executor =
//        (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Math.max(1, _cpus-1));
    (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
    /**
     * Sorted set for submitted periodic workers - decreasing by period
     */
    private final SortedSet<PersistDataWorker> _submittedWorkers =
        Sets.newTreeSet(new Comparator<PersistDataWorker>() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public int compare(@Nonnull final PersistDataWorker arg0,
                                               @Nonnull final PersistDataWorker arg1) {
                                return Long.valueOf(arg0.getPeriod()).compareTo(Long.valueOf(arg1.getPeriod()));
                            }
                        });

    /**
     * Entity managing access to blocking queue for consumer-producer pattern of submitted SQL
     * statements.
     */
    private final SqlStatementBatch _sqlStatementBatch;



    /**
     * DAOs.
     */
    private IArchiveChannelDao _archiveChannelDao;
    private IArchiveChannelGroupDao _archiveChannelGroupDao;
    private IArchiveEngineDao _archiveEngineDao;
    private IArchiverMgmtDao _archiverMgmtDao;
    private IArchiveSampleDao _archiveSampleDao;
    private IArchiveSampleModeDao _archiveSampleModeDao;
    private IArchiveSeverityDao _archiveSeverityDao;
    private IArchiveStatusDao _archiveStatusDao;

    /**
     * The datasource that specifies the connections.
     */
    private MysqlDataSource _dataSource;

    /**
     * Any thread owns a connection.
     */
    private final ThreadLocal<Connection> _archiveConnection =
        new ThreadLocal<Connection>();

    /**
     * Prefs from plugin_customization.
     */
    private String _prefHost;
    private String _prefFailoverHost;
    private String _prefUser;
    private String _prefPassword;
    private Integer _prefPort;
    private String _prefDatabaseName;
    private Integer _prefPeriodInMS;
    private Integer _prefMaxAllowedPacketInBytes;

    private String _prefMailHost;
    private String _prefEmailReceiver;


    /**
     * Constructor.
     */
    private ArchiveDaoManager() {

        loadAndCheckPreferences();
        _dataSource = createDataSource();

        _sqlStatementBatch = SqlStatementBatch.INSTANCE;

        addGracefullyShutdownHook();

        submitNewPersistDataWorker();
    }


    private void loadAndCheckPreferences() {
        _prefHost = HOST.getValue();
        _prefFailoverHost = FAILOVER_HOST.getValue();
        _prefPort = PORT.getValue();
        _prefDatabaseName = DATABASE_NAME.getValue();
        _prefUser = USER.getValue();
        _prefPassword = PASSWORD.getValue();

        _prefPeriodInMS = MySQLArchiveServicePreference.PERIOD.getValue();
        if (_prefPeriodInMS < MIN_PERIOD_MS || _prefPeriodInMS > MAX_PERIOD_MS) {
            LOG.warn("Initial interval in seconds for the PersistDataWorker thread out of recommended bounds [" +
                     MIN_PERIOD_MS + "," + MAX_PERIOD_MS+ "]." +
                     "Set to " + DEFAULT_PERIOD_MS + "ms.");
            _prefPeriodInMS = DEFAULT_PERIOD_MS;
        }

        final int maxAllowedPacketInKB = MAX_ALLOWED_PACKET.getValue();

        // TODO (bknerr) : test code for minimum size
        if (maxAllowedPacketInKB < 1 || maxAllowedPacketInKB > 64 * KBYTE_SIZE) {
            LOG.warn("MaxAllowedPacket connection parameter out of recommended range [" +
                     KBYTE_SIZE + "," + 64 * KBYTE_SIZE + "]kb. Set to " + 16 * KBYTE_SIZE + " kb.");
            _prefMaxAllowedPacketInBytes = 16 * KBYTE_SIZE * KBYTE_SIZE;
        } else {
            _prefMaxAllowedPacketInBytes = maxAllowedPacketInKB * KBYTE_SIZE;
        }

        _prefMailHost = SMTP_HOST.getValue();
        _prefEmailReceiver = EMAIL_ADDRESS.getValue();
    }

    @Nonnull
    private MysqlDataSource createDataSource() {

        final MysqlDataSource ds = new MysqlDataSource();
        String hosts = _prefHost;
        if (!StringUtil.isBlank(_prefFailoverHost)) {
            hosts += "," + _prefFailoverHost;
        }
        ds.setServerName(hosts);
        ds.setPort(_prefPort);
        ds.setDatabaseName(_prefDatabaseName);
        ds.setUser(_prefUser);
        ds.setPassword(_prefPassword);
        ds.setFailOverReadOnly(false);
        ds.setMaxAllowedPacket(_prefMaxAllowedPacketInBytes);

        return ds;
    }

    private void addGracefullyShutdownHook() {
        /**
         * Add shutdown hook.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                LOG.info("Killed");
                // submit an executor that processes immediately what's left in the queue
                _executor.scheduleAtFixedRate(new PersistDataWorker("ShutdownWorker",
                                                                    _sqlStatementBatch.getQueue(),
                                                                    Integer.valueOf(0)),
                                              0L, 0L, TimeUnit.SECONDS);
                _executor.shutdown(); // gracefully shutdown (wait for all formerly submitted workers to finish
                try {
                    if (!_executor.awaitTermination(_prefPeriodInMS + 1, TimeUnit.SECONDS)) {
                        LOG.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                        final List<Runnable> droppedTasks = _executor.shutdownNow();
                        LOG.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks might not have been executed."); //optional **
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void submitNewPersistDataWorker() {
        final PersistDataWorker newWorker = new PersistDataWorker("FixedRateWorker:" + _submittedWorkers.size(),
                                                                  _sqlStatementBatch.getQueue(),
                                                                  _prefPeriodInMS);
        _executor.scheduleAtFixedRate(newWorker,
                                      0,
                                      newWorker.getPeriod(),
                                      TimeUnit.SECONDS);
        _submittedWorkers.add(newWorker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void submitStatementsToBatch(@Nonnull final List<String> stmts) {
        for (final String stmt : stmts) {
            submitStatementToBatch(stmt);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void submitStatementToBatch(@Nonnull final String stmt) {
        synchronized (this) {
            if (anotherWorkerRequired()) {
                submitNewPersistDataWorker();
            }
            _sqlStatementBatch.submitStatement(stmt);
        }
    }

    /**
     * Checks whether we need another worker.
     * First check is whether the blocking queue of statements exceeds the max allowed packet size.
     * If so, is there still space in the thread pool for another periodic task.
     * If not so, is there the possibility to replace a rarely scheduled task with a task with higher
     * frequency.
     * If not so, FIXME (bknerr) : start a data rescue worker to save the stuff to disc and inform the staff per email
     * @return
     */
    private boolean anotherWorkerRequired() {
        // Is it necessary?
        if (_sqlStatementBatch.sizeInBytes() > _prefMaxAllowedPacketInBytes) {
            // Yes, is still space in the pool for another worker?
            final int poolSize = _executor.getPoolSize();
            final int corePoolSize = _executor.getCorePoolSize();
            if (poolSize < corePoolSize) {
                return true; // Yes
            } else {
                // No, but perhaps we could enhance the frequency of the scheduled tasks?
                final Iterator<PersistDataWorker> it = _submittedWorkers.iterator();
                final PersistDataWorker oldestWorker = it.next();
                final long period = oldestWorker.getPeriod();
                if (Long.valueOf(period).intValue() <= MIN_PERIOD_MS) {
                    // No
                    // FIXME (bknerr) : handle pool and thread frequency exhaustion
                    // notify staff, rescue data to disc with dedicated worker
                    return false;
                }
                // Yes, lower the frequency and remove the oldest periodic worker, return true
                _prefPeriodInMS = Math.max(_prefPeriodInMS>>1, MIN_PERIOD_MS);
                _executor.remove(oldestWorker);
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * FIXME (bknerr) : data rescue on failover fail not yet implemented!
     * @param statements
     */
    void rescueData(@Nonnull final List<String> statements) {
        LOG.info("Failed statements" + statements.size());

        EMailSender mailer;
        try {
            mailer = new EMailSender(_prefMailHost,
                                     "DontReply@MySQLArchiver",
                                     _prefEmailReceiver,
                                     "[MySQL archiver notification]: Failed failover");
            mailer.addText("Statements rescued:\n");
            for (final String stmt : statements) {
                //mailer.addText(stmt);
            }
            mailer.close();
        } catch (final IOException e) {
            // TODO (bknerr) : handle exceptions on notifications
            e.printStackTrace();
        }

    }


    /**
     * Connects with the RDB instance for the given datasource.
     *
     * An existing connection is closed and an new connection is established.
     *
     * @param ds the mysql data source
     * @return connection the newly established connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    public Connection connect(@Nonnull final MysqlDataSource ds) throws ArchiveConnectionException {

        Connection connection = _archiveConnection.get();
        try {
            if (connection != null) { // close existing connection
                _archiveConnection.set(null);
                connection.close();
            }
            // Get class loader to find the driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = ds.getConnection();

            if (connection != null) {
                final DatabaseMetaData meta = connection.getMetaData();
                if (meta != null) {
                    // Constructor call -> LOG.debug not possible, not yet initialised
                    CentralLogger.getInstance().getLogger(ArchiveDaoManager.class).debug("MySQL connection:\n" +
                              meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                } else {
                    // Constructor call -> LOG.debug not possible, not yet initialised
                    CentralLogger.getInstance().getLogger(ArchiveDaoManager.class).debug("No meta data for MySQL connection");
                }
                // set to true to enable failover to other host
                connection.setAutoCommit(true);
                _archiveConnection.set(connection);
            }
        } catch (final Exception e) {
            handleExceptions(e);
        }
        if (connection == null || StringUtil.isBlank(_prefDatabaseName)) {
            throw new ArchiveConnectionException("Connection could not be established or database name is not set.", null);
        }
        return connection;
    }

    /**
     * To reduce the readability of the invoking method. Catches checked exceptions, wraps them in
     * dedicated abstraction level exception. Rethrows any other exception as new RuntimeException.
     *
     * @param e
     * @throws Throwable
     */
    private void handleExceptions(@Nonnull final Exception e) throws ArchiveConnectionException {
        try {
            throw e;
        } catch (final InstantiationException ie) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, ie);
        } catch (final IllegalAccessException iae) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, iae);
        } catch (final ClassNotFoundException cfe) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, cfe);
        } catch (final SQLException se) {
            throw new ArchiveConnectionException(ARCHIVE_CONNECTION_EXCEPTION_MSG, se);
        } catch (final Exception re) {
            throw new RuntimeException(re);
        }
    }

    /**
     * Disconnects the connection for the owning thread.
     * @throws ArchiveConnectionException
     */
    public void disconnect() throws ArchiveConnectionException {
        final Connection connection = _archiveConnection.get();
        if (connection != null) {
            try {
                _executor.shutdown(); // handles all already submitted tasks
                _executor.awaitTermination(MIN_PERIOD_MS, TimeUnit.MILLISECONDS);
                connection.close();
                _archiveConnection.set(null);
            } catch (final SQLException e) {
                throw new ArchiveConnectionException("Archive disconnection failed!", e);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * Returns the current connection for the owning thread.
     * This method is invoked by the dedicated daos to retrieve their connection.
     * A connection's datasource is configured via the plugin_customization.ini
     *
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Override
    @Nonnull
    public Connection getConnection() throws ArchiveConnectionException {
        final Connection connection = _archiveConnection.get();
        if (connection == null) {
            // the calling thread has not yet a connection registered.
            return connect(_dataSource);
        }
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public String getDatabaseName() {
        return _prefDatabaseName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveChannelDao getChannelDao() {
        if (_archiveChannelDao == null) {
            _archiveChannelDao = new ArchiveChannelDaoImpl(this);
        }
        return _archiveChannelDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiverMgmtDao getArchiverMgmtDao() {
        if (_archiverMgmtDao == null) {
            _archiverMgmtDao  = new ArchiverMgmtDaoImpl(this);
        }
        return _archiverMgmtDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveChannelGroupDao getChannelGroupDao() {
        if (_archiveChannelGroupDao == null) {
            _archiveChannelGroupDao = new ArchiveChannelGroupDaoImpl(this);
        }
        return _archiveChannelGroupDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveSampleModeDao getSampleModeDao() {
        if (_archiveSampleModeDao == null) {
            _archiveSampleModeDao = new ArchiveSampleModeDaoImpl(this);
        }
        return _archiveSampleModeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveSampleDao getSampleDao() {
        if (_archiveSampleDao == null) {
            _archiveSampleDao = new ArchiveSampleDaoImpl(this);
        }
        return _archiveSampleDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveEngineDao getEngineDao() {
        if (_archiveEngineDao == null) {
            _archiveEngineDao = new ArchiveEngineDaoImpl(this);
        }
        return _archiveEngineDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveSeverityDao getSeverityDao() {
        if (_archiveSeverityDao == null) {
            _archiveSeverityDao = new ArchiveSeverityDaoImpl(this);
        }
        return _archiveSeverityDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveStatusDao getStatusDao() {
        if (_archiveStatusDao == null) {
            _archiveStatusDao = new ArchiveStatusDaoImpl(this);
        }
        return _archiveStatusDao;
    }
}
