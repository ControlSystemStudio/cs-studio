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

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.archivermgmt.IArchiverMgmtEntry;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;

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
     * Converter function to single sql VALUE, i.e. comma separated strings embraced by parentheses.
     * TODO (bknerr) : extract to be used by all VALUE assemblers
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class MonitorStates2SqlValue implements Function<IArchiverMgmtEntry, String> {
        /**
         * Constructor.
         */
        public MonitorStates2SqlValue() {
            // Empty
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String apply(@Nonnull final IArchiverMgmtEntry from) {

            return "(" +
                   Joiner.on(",").join(from.getChannelId().longValue(),
                                       "'" + from.getStatus().name() + "'", // TODO (bknerr) : once we use hibernate...
                                       from.getEngineId().longValue(),
                                       "'" + from.getTimestamp().formatted() + "'",
                                       "'"  + from.getInfo() + "'") +
                    ")";
        }
    }
    private static final MonitorStates2SqlValue M2S_FUNC = new MonitorStates2SqlValue();

    /**
     * Constructor.
     */
    public ArchiverMgmtDaoImpl() {
        super();
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
    public IArchiverMgmtEntry createMgmtEntry(@Nonnull final IArchiverMgmtEntry entry) throws ArchiveDaoException {
        final String sqlValue = M2S_FUNC.apply(entry);
        final String stmtStr = createMgmtEntryUpdateStmtPrefix(getDatabaseName()) + sqlValue;

        getEngineMgr().submitStatementToBatch(stmtStr);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createMgmtEntries(@Nonnull final Collection<IArchiverMgmtEntry> monitorStates) throws ArchiveDaoException {

        final String values = Joiner.on(",").join(Iterables.transform(monitorStates, M2S_FUNC));
        final String stmtStr = createMgmtEntryUpdateStmtPrefix(getDatabaseName()) + values;

        getEngineMgr().submitStatementToBatch(stmtStr);

        return true;
    }

}
