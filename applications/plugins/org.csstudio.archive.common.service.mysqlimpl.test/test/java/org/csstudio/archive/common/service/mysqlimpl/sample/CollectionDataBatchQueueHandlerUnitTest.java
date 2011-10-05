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

import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.CHANNEL_ID_5TH;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.SAMPLE_ARRAY_D;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.START;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.internal.Lists;

/**
 * Test for {@link CollectionDataSampleBatchQueueHandler}.
 *
 * @author bknerr
 * @since 10.08.2011
 */
public class CollectionDataBatchQueueHandlerUnitTest extends AbstractDaoTestSetup {

    private static IArchiveChannelDao CHANNEL_DAO;
    private static IArchiveSampleDao SAMPLE_DAO;

    @BeforeClass
    public static void setupDao() {
        CHANNEL_DAO = new ArchiveChannelDaoImpl(HANDLER, PERSIST_MGR);
        SAMPLE_DAO = new ArchiveSampleDaoImpl(HANDLER, PERSIST_MGR, CHANNEL_DAO);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testConvertStatementToString() throws ArchiveDaoException {
        final CollectionDataSampleBatchQueueHandler handler = new CollectionDataSampleBatchQueueHandler("archive_test");
        final Collection<String> stmtStrings =
            handler.convertToStatementString(Lists.newArrayList((ArchiveMultiScalarSample) SAMPLE_ARRAY_D));
        Assert.assertTrue(stmtStrings.size() == 1);
        final String stmtStr = stmtStrings.iterator().next();


        try {
            final Connection connection = HANDLER.createConnection();
            final Statement stmt = connection.createStatement();
            stmt.executeUpdate(stmtStr);
            stmt.close();
            connection.close();
        } catch (final Exception e) {
            Assert.fail();
        }

        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.RAW_MULTI_SCALAR,
                                       CHANNEL_ID_5TH,
                                       START.minusMillis(1L),
                                       START.plusMillis(1L));


        Assert.assertTrue(samples.size() == 1);
        final IArchiveSample<Serializable, ISystemVariable<Serializable>> sample =
            samples.iterator().next();
        Assert.assertTrue(sample.getValue() instanceof ArrayList);

        final ArrayList val = (ArrayList) sample.getValue();
        for (int i = 0; i < SAMPLE_ARRAY_D.getValue().size(); i++) {
            Assert.assertEquals(SAMPLE_ARRAY_D.getValue().get(i), val.get(i));
        }
    }
}
