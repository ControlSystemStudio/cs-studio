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
package org.csstudio.archive.common.service.mysqlimpl.channelstatus;

import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;

import com.google.common.base.Joiner;

/**
 *
 * @author baschtl
 * @since Feb 26, 2011
 */
public class ArchiveChannelStatusDaoImpl extends AbstractArchiveDao implements IArchiveChannelStatusDao {

    private static final String RETRIEVAL_FAILED = "Creation of channel status entry failed.";

    public static final String TAB = "channel_status";

    private static final String _insertEntryStmtPrefix =
        "INSERT INTO " + getDaoMgr().getDatabaseName() + "." + TAB +
                     " channel_id, connected, info, timestamp " +
                     "VALUES ";

    public ArchiveChannelStatusDaoImpl() {
        super();
    }

    @Override
    public void createChannelStatus(final ArchiveChannelStatus entry) throws ArchiveDaoException {
        final String stmtStr = Joiner.on(",").join(_insertEntryStmtPrefix,
                                                   entry.getChannelId().intValue(),
                                                   entry.isConnected().toString(),
                                                   "'" + entry.getInfo() + "'",
                                                   "'" + entry.getTime().formatted() + "'");
        getEngineMgr().submitStatementToBatch(stmtStr);
    }

}
