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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.internal.Lists;

/**
 * Integration test for multi scalar samples in {@link ArchiveSampleDaoImpl}.
 *
 * @author bknerr
 * @since 10.08.2011
 */
public class ArchiveMultiScalarSampleDaoCreateUnitTest extends AbstractDaoTestSetup {

    private static final ArchiveChannelId CHANNEL_ID = new ArchiveChannelId(5L);
    private static IArchiveSampleDao SAMPLE_DAO;
    private static IArchiveChannelDao CHANNEL_DAO;
    private static TimeInstant NOW;
    private static IArchiveSample<ArrayList<Double>, ISystemVariable<ArrayList<Double>>> SAMPLE_D;
    private static final ArrayList<Double> SAMPLE_D_VAL = Lists.newArrayList(5.0, 4.0);

    /**
     * Constructor.
     */
    public ArchiveMultiScalarSampleDaoCreateUnitTest() {
        super(false);
    }

    @BeforeClass
    public static void setup() {
        NOW = TimeInstantBuilder.fromNow();
        SAMPLE_D = new ArchiveMultiScalarSample<Double,
                                                ArrayList<Double>,
                                                ISystemVariable<ArrayList<Double>>>(
                                                        CHANNEL_ID,
                                                        new EpicsSystemVariable<ArrayList<Double>>("fuup",
                                                                                                   SAMPLE_D_VAL,
                                                                                                   ControlSystem.EPICS_DEFAULT,
                                                                                                   NOW,
                                                                                                   EpicsAlarm.UNKNOWN),
                                                        EpicsAlarm.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeHook() throws ArchiveConnectionException, SQLException {
        CHANNEL_DAO = new ArchiveChannelDaoImpl(HANDLER, PERSIST_MGR);
        SAMPLE_DAO = new ArchiveSampleDaoImpl(HANDLER, PERSIST_MGR, CHANNEL_DAO);
    }

    @Test
    public void testCreateDoubleSample() throws ArchiveDaoException, InterruptedException {
        SAMPLE_DAO.createSamples(Collections.singleton(SAMPLE_D));

        Thread.sleep(2500);

        final IArchiveChannel channel = CHANNEL_DAO.retrieveChannelById(CHANNEL_ID);
        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples =
            SAMPLE_DAO.retrieveSamples(null, channel, NOW.minusMillis(1L), NOW.plusMillis(1L));

        Assert.assertTrue(samples.size() == 1);

        final IArchiveSample<Serializable,ISystemVariable<Serializable>> sample =
            samples.iterator().next();
        Assert.assertEquals(CHANNEL_ID, sample.getChannelId());
        Assert.assertTrue(sample.getValue() instanceof ArrayList);

        @SuppressWarnings("rawtypes")
        final ArrayList result = (ArrayList) sample.getValue();
        Assert.assertEquals(SAMPLE_D_VAL.size(), result.size());
        for (int i = 0; i < SAMPLE_D_VAL.size(); i++) {
            Assert.assertEquals(SAMPLE_D_VAL.get(i), result.get(i));
        }
    }
}
