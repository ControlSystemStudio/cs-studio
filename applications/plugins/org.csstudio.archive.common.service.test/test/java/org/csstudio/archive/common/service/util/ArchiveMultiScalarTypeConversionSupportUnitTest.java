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
package org.csstudio.archive.common.service.util;

import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_ELEM_SEP;
import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_PREFIX;
import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_SUFFIX;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Test of multi scalar type conversion in {@link ArchiveTypeConversionSupport}.
 *
 * @author bknerr
 * @since 04.08.2011
 */
public class ArchiveMultiScalarTypeConversionSupportUnitTest {

    @BeforeClass
    public static void setup() {
        ArchiveTypeConversionSupport.install();
    }

    @Test
    public void testMultiScalarEmptyConversion() {

        final Serializable valuesEmpty = Lists.newArrayList();
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesEmpty);
            Assert.assertEquals("", archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail("Empty string list should be convertible to empty string");
        }
    }

    @Test(expected=TypeSupportException.class)
    public void testMultiScalarMisMatchConversion() throws TypeSupportException {
        ArchiveTypeConversionSupport.fromArchiveString(Integer.class, "theshapeofpunk,tocome");
    }

    @Test
    public void testMultiScalarStringConversion() {
        final Serializable valuesS = Lists.newArrayList("modest", "mouse");
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesS);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "modest\\,mouse" + ARCHIVE_COLLECTION_SUFFIX,
                                archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testDeSerialization() throws TypeSupportException {
        Serializable start = Lists.newArrayList(Double.valueOf(1.0), Double.valueOf(2.0));
        byte[] byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        final ArrayList result = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(Double.valueOf(1.0), result.get(0));
        Assert.assertEquals(Double.valueOf(2.0), result.get(1));

        start = new CopyOnWriteArrayList<Double>(new Double[]{Double.valueOf(1.0), Double.valueOf(2.0)});
        byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        final CopyOnWriteArrayList result2 = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(Double.valueOf(1.0), result2.get(0));
        Assert.assertEquals(Double.valueOf(2.0), result2.get(1));

        start = Sets.<EpicsEnum>newHashSet(Arrays.asList(new EpicsEnum[] {EpicsEnum.createFromRaw(-1)}));
        byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        final HashSet result3 = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(EpicsEnum.createFromRaw(-1), result3.iterator().next());
    }

    @Test
    public void testMultiScalarIntegerConversion() {
        final Serializable valuesI = Lists.newArrayList(1, 2, 3, 4);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesI);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1\\,2\\,3\\,4" + ARCHIVE_COLLECTION_SUFFIX, archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarDoubleConversion() {
        final Serializable valuesD = Lists.newArrayList(1.0, 2.0);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesD);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1.0\\,2.0" + ARCHIVE_COLLECTION_SUFFIX, archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarToHashSetFloatConversion() {
        final Serializable valuesF = Lists.newArrayList(1.0F, 2.0F);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesF);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1.0\\,2.0" + ARCHIVE_COLLECTION_SUFFIX, archiveString);

            final Serializable coll = ArchiveTypeConversionSupport.fromArchiveString("HashSet<Float>", archiveString);
            Assert.assertNotNull(coll instanceof HashSet);
            @SuppressWarnings("unchecked")
            final HashSet<Float> set = (HashSet<Float>) coll;

            final Iterator<Float> iterator = set.iterator();
            Assert.assertTrue(Float.valueOf(1.0F).equals(iterator.next()));
            Assert.assertTrue(Float.valueOf(2.0F).equals(iterator.next()));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarToArrayListByteConversion() {
        final Serializable valuesB = Lists.newArrayList(Byte.valueOf("127"),
                                                            Byte.valueOf("-128"));
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesB);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "127\\,-128" + ARCHIVE_COLLECTION_SUFFIX, archiveString);

            final Serializable coll =
                ArchiveTypeConversionSupport.fromArchiveString("ArrayList<Byte>", archiveString);
            Assert.assertNotNull(coll instanceof ArrayList);

            @SuppressWarnings("unchecked")
            final ArrayList<Byte> list = (ArrayList<Byte>) coll;

            final Iterator<Byte> iterator = list.iterator();
            Assert.assertEquals(Byte.valueOf("127"), iterator.next());
            Assert.assertEquals(Byte.valueOf("-128"), iterator.next());

        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarCollectionConversion() {
        final Serializable valuesB1 = Lists.newArrayList(Byte.valueOf("127"),
                                                             Byte.valueOf("-128"));
        final Serializable valuesB2 = Lists.newArrayList(Byte.valueOf("0"),
                                                             Byte.valueOf("1"));

        final Serializable valuesB = Lists.newArrayList(valuesB1, valuesB2);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesB);
            final String valuesB1String = ARCHIVE_COLLECTION_PREFIX + "127\\,-128" + ARCHIVE_COLLECTION_SUFFIX;
            final String valuesB2String = ARCHIVE_COLLECTION_PREFIX + "0\\,1" + ARCHIVE_COLLECTION_SUFFIX;
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + valuesB1String +  "\\," +
                                                            valuesB2String +
                                ARCHIVE_COLLECTION_SUFFIX, archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarEnumConversion() {
        final Serializable valuesE = Lists.newArrayList(EpicsEnum.createFromRaw(1),
                                                        EpicsEnum.createFromStateName("second"));
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesE);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX +
                                EpicsEnum.RAW + EpicsEnum.SEP + "1" +
                                ARCHIVE_COLLECTION_ELEM_SEP +
                                EpicsEnum.STATE + "(0)" + EpicsEnum.SEP + "second"+
                                ARCHIVE_COLLECTION_SUFFIX, archiveString);
            @SuppressWarnings("unchecked")
            final Vector<EpicsEnum> enums =
                ArchiveTypeConversionSupport.fromArchiveString(Vector.class,
                                                               EpicsEnum.class,
                                                               archiveString);
            Assert.assertEquals(2, enums.size());

            final Iterator<EpicsEnum> iterator = enums.iterator();
            final EpicsEnum first = iterator.next();
            Assert.assertEquals(Integer.valueOf(1), first.getRaw());

            final EpicsEnum second = iterator.next();
            Assert.assertEquals("second", second.getState());
            Assert.assertEquals(Integer.valueOf(0), second.getStateIndex());

        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }
}
