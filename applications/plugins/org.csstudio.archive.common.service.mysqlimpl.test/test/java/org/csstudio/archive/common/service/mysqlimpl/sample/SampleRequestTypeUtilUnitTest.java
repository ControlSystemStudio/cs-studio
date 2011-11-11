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

import java.util.LinkedHashSet;

import junit.framework.Assert;

import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link SampleRequestTypeUtil}.
 *
 * @author bknerr
 * @since 10.08.2011
 */
public class SampleRequestTypeUtilUnitTest {

    @BeforeClass
    public static final void setup() {
        ArchiveTypeConversionSupport.install();
    }

    @Test
    public void testScalarNotOptimizable() throws TypeSupportException {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        DesyArchiveRequestType type =
            SampleRequestTypeUtil.determineRequestType(String.class, now, now.plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);

        type = SampleRequestTypeUtil.determineRequestType(String.class, now, now.plusMillis(1000L*60L*60L*24L*50L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);
    }

    @Test
    public void testScalarOptimizable() throws TypeSupportException {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        DesyArchiveRequestType type =
            SampleRequestTypeUtil.determineRequestType(Double.class, now, now.plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);

        type = SampleRequestTypeUtil.determineRequestType(Double.class, now, now.plusDuration(Duration.standardDays(1)));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);

        type = SampleRequestTypeUtil.determineRequestType(Double.class, now, now.plusDuration(Duration.standardDays(1)).plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.AVG_PER_MINUTE, type);

        type = SampleRequestTypeUtil.determineRequestType(Double.class, now, now.plusDuration(Duration.standardDays(45)));
        Assert.assertEquals(DesyArchiveRequestType.AVG_PER_MINUTE, type);

        type = SampleRequestTypeUtil.determineRequestType(Double.class, now, now.plusDuration(Duration.standardDays(45)).plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.AVG_PER_HOUR, type);
    }


    @Test
    public void testMultiScalar() throws TypeSupportException {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        DesyArchiveRequestType type =
            SampleRequestTypeUtil.determineRequestType(LinkedHashSet.class, now, now.plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);
        type = SampleRequestTypeUtil.determineRequestType(LinkedHashSet.class, now, now.plusDuration(Duration.standardDays(1)).plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);
        type = SampleRequestTypeUtil.determineRequestType(LinkedHashSet.class, now, now.plusDuration(Duration.standardDays(45)).plusMillis(1L));
        Assert.assertEquals(DesyArchiveRequestType.RAW, type);
    }
}
