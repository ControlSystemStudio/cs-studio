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
package org.csstudio.archive.common.service.mysqlimpl.archivermgmt;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.archivermgmt.IArchiverMgmtEntry;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 02.02.2011
 */
public class ArchiverMgmtDaoImpl extends AbstractArchiveDao implements IArchiverMgmtDao {

    /**
     * Constructor.
     * @param mgr
     */
    public ArchiverMgmtDaoImpl(@Nonnull final ArchiveDaoManager mgr) {
        super(mgr);
    }

    @Nonnull
    private String createMgmtEntryUpdateStmtPrefix(@Nonnull final String database) {
        return "INSERT INTO " + database + ".archiver_mgmt (channel_id, monitor_mode, engine_id, time, info) VALUES ";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiverMgmtEntry createMgmtEntry(@Nonnull final IArchiverMgmtEntry entry) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createMgmtEntries(final Collection<IArchiverMgmtEntry> monitorStates) throws ArchiveDaoException {

        final String values = Joiner.on(",").join(Iterables.transform(monitorStates,
                                                                      new Function<IArchiverMgmtEntry, String>() {
                                                                          /**
                                                                           * {@inheritDoc}
                                                                           */
                                                                           @Override
                                                                           public String apply(final IArchiverMgmtEntry from) {

                                                                               return "(" +
                                                                                      Joiner.on(",").join(from.getChannelId().longValue(),
                                                                                                          from.getStatus().name(),
                                                                                                          from.getEngineId().longValue(),
                                                                                                          from.getTimestamp().formatted(),
                                                                                                          from.getInfo()) +
                                                                                      ")";
                                                                           }
                                                                       }));
        final String stmtStr = createMgmtEntryUpdateStmtPrefix(getDaoMgr().getDatabaseName()) + values;

        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();//.prepareStatement(stmtStr);
            stmt.execute(stmtStr);
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException("Creation of monitor states failed.", e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException("Creation of monitor states failed.", e);
        } finally {
            closeStatement(stmt, "Closing of statement for monitor states of channels failed.");
        }


        return false;
    }

}
