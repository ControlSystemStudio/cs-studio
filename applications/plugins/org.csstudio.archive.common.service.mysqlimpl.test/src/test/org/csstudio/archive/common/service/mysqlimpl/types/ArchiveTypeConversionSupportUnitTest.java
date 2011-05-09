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
package org.csstudio.archive.common.service.mysqlimpl.types;

import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_ELEM_SEP;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_PREFIX;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_SUFFIX;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_TUPLE_PREFIX;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_TUPLE_SEP;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.ARCHIVE_TUPLE_SUFFIX;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.collectionEmbrace;
import static org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport.collectionRelease;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test, what else.
 *
 * @author bknerr
 * @since 10.12.2010
 */
public class ArchiveTypeConversionSupportUnitTest {

    @Before
    public void setup() {
        ArchiveTypeConversionSupport.install();
    }

    @Test
    public void testEmbraceRelease() {

        final String empty = collectionEmbrace("");
        Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX +
                            ARCHIVE_COLLECTION_SUFFIX, empty);
        Assert.assertEquals("", collectionRelease(empty));


        final String xxx = collectionEmbrace("xxx");
        Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "xxx" +
                            ARCHIVE_COLLECTION_SUFFIX, xxx);
        Assert.assertEquals("xxx", collectionRelease(xxx));

        final String yyy = collectionEmbrace("(()()d(x)");
        Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "(()()d(x)" +
                            ARCHIVE_COLLECTION_SUFFIX, yyy);
        Assert.assertEquals("(()()d(x)", collectionRelease(yyy));

        Assert.assertEquals(null, collectionRelease(""));
        Assert.assertEquals(null, collectionRelease(ARCHIVE_COLLECTION_PREFIX));
        Assert.assertEquals(null, collectionRelease(ARCHIVE_COLLECTION_SUFFIX));
        Assert.assertEquals(null, collectionRelease("x" + ARCHIVE_COLLECTION_SUFFIX));
        Assert.assertEquals(null, collectionRelease(ARCHIVE_COLLECTION_PREFIX + "s"));
    }

    @Test
    public void testScalarDoubleArchiveStringConversion() {

        try {
            final Double d = Double.valueOf(1.01010101010101010100101010000010010);
            final String sd = d.toString();
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(d);
            Assert.assertTrue(archiveString.equals(sd));
            final Double dFromA = ArchiveTypeConversionSupport.fromArchiveString(Double.class, archiveString);
            Assert.assertNotNull(dFromA);
            Assert.assertTrue(dFromA.equals(d));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarIntegerArchiveStringConversion() {
        try {
            final Integer i = Integer.valueOf(-1234567);
            final String si = i.toString();
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(i);
            Assert.assertTrue(archiveString.equals(si));
            final Integer iFromA = ArchiveTypeConversionSupport.fromArchiveString(Integer.class, archiveString);
            Assert.assertNotNull(iFromA);
            Assert.assertTrue(iFromA.equals(i));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarByteArchiveStringConversion() {
        try {
            final Byte b = Byte.valueOf((byte) -128);
            final String sb = b.toString();
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(b);
            Assert.assertTrue(archiveString.equals(sb));
            final Byte bFromA = ArchiveTypeConversionSupport.fromArchiveString(Byte.class, archiveString);
            Assert.assertNotNull(bFromA);
            Assert.assertTrue(bFromA.equals(b));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testScalarFloatArchiveStringConversion() {
        try {
            final Float f = Float.valueOf(44.44F);
            final String sf = f.toString();
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(f);
            Assert.assertTrue(archiveString.equals(sf));
            final Float fFromA = ArchiveTypeConversionSupport.fromArchiveString(Float.class, archiveString);
            Assert.assertNotNull(fFromA);
            Assert.assertTrue(fFromA.equals(f));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarStringArchiveStringConversion() {
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString("test me");
            Assert.assertTrue(archiveString.equals("test me"));
            final String sFromA = ArchiveTypeConversionSupport.fromArchiveString(String.class, archiveString);
            Assert.assertNotNull(sFromA);
            Assert.assertTrue(sFromA.equals(archiveString));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarEnumArchiveStringConversion() {
        try {
                {
                    final EpicsEnum t = EpicsEnum.createFromRaw(3);
                    final String archiveString = ArchiveTypeConversionSupport.toArchiveString(t);
                    Assert.assertTrue(archiveString.equals(EpicsEnum.RAW + EpicsEnum.SEP + "3"));
                    final EpicsEnum tFromA = ArchiveTypeConversionSupport.fromArchiveString(EpicsEnum.class, archiveString);
                    Assert.assertNotNull(tFromA);
                    Assert.assertEquals(Integer.valueOf(3), tFromA.getRaw());
                }
                {
                    final EpicsEnum t = EpicsEnum.createFromState("MyState");
                    final String archiveString = ArchiveTypeConversionSupport.toArchiveString(t);
                    Assert.assertTrue(archiveString.equals(EpicsEnum.STATE + EpicsEnum.SEP + "MyState"));
                    final EpicsEnum tFromA = ArchiveTypeConversionSupport.fromArchiveString(EpicsEnum.class, archiveString);
                    Assert.assertNotNull(tFromA);
                    Assert.assertEquals("MyState", tFromA.getState());
                    
                }
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarEmptyConversion() {

        final List<String> valuesEmpty = Lists.newArrayList();
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesEmpty);
            Assert.assertEquals("", archiveString);

            ArchiveTypeConversionSupport.fromArchiveString("IDoNotExist", "Iwasborninafactory,,,,whohoo");
        } catch (final TypeSupportException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testMultiScalarMisMatchConversion() {
        try {
            ArchiveTypeConversionSupport.fromArchiveString(Integer.class, "theshapeofpunk,tocome");
        } catch (final TypeSupportException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testMultiScalarStringConversion() {
        final Collection<String> valuesS = Lists.newArrayList("modest", "mouse");
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesS);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "modest\\,mouse" + ARCHIVE_COLLECTION_SUFFIX,
                                archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarIntegerConversion() {
        final Collection<Integer> valuesI = Lists.newArrayList(1,2,3,4);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesI);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1\\,2\\,3\\,4" + ARCHIVE_COLLECTION_SUFFIX, archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testMultiScalarDoubleConversion() {
        final Collection<Double> valuesD = Lists.newArrayList(1.0,2.0);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesD);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1.0\\,2.0" + ARCHIVE_COLLECTION_SUFFIX, archiveString);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarFloatConversion() {
        final Collection<Float> valuesF = Lists.newArrayList(1.0F, 2.0F);
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesF);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "1.0\\,2.0" + ARCHIVE_COLLECTION_SUFFIX, archiveString);

            final Collection<Float> coll = ArchiveTypeConversionSupport.fromArchiveString("HashSet<Float>", archiveString);

            final Iterator<Float> iterator = coll.iterator();
            Assert.assertTrue(iterator.next().equals(1.0F));
            Assert.assertTrue(iterator.next().equals(2.0F));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarByteConversion() {
        final Collection<Byte> valuesB = Lists.newArrayList(Byte.valueOf("127"),
                                                            Byte.valueOf("-128"));
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesB);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX + "127\\,-128" + ARCHIVE_COLLECTION_SUFFIX, archiveString);


            final Collection<Byte> coll =
                ArchiveTypeConversionSupport.fromArchiveString("ArrayList<Byte>", archiveString);

            final Iterator<Byte> iterator = coll.iterator();
            Assert.assertEquals(Byte.valueOf("127"), iterator.next());
            Assert.assertEquals(Byte.valueOf("-128"), iterator.next());

        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarCollectionConversion() {
        final Collection<Byte> valuesB1 = Lists.newArrayList(Byte.valueOf("127"),
                                                             Byte.valueOf("-128"));
        final Collection<Byte> valuesB2 = Lists.newArrayList(Byte.valueOf("0"),
                                                             Byte.valueOf("1"));

        @SuppressWarnings("unchecked")
        final Collection<Collection<Byte>> valuesB = Lists.newArrayList(valuesB1, valuesB2);
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
        final Collection<EpicsEnum> valuesE = Lists.newArrayList(EpicsEnum.createFromRaw(1),
                                                                 EpicsEnum.createFromState("second"));
        try {
            final String archiveString = ArchiveTypeConversionSupport.toArchiveString(valuesE);
            Assert.assertEquals(ARCHIVE_COLLECTION_PREFIX +
                                EpicsEnum.RAW + EpicsEnum.SEP + "1" +
                                ARCHIVE_COLLECTION_ELEM_SEP +
                                EpicsEnum.STATE + EpicsEnum.SEP + "second"+
                                ARCHIVE_COLLECTION_SUFFIX, archiveString);
            final Vector<EpicsEnum> enums =
                (Vector<EpicsEnum>) ArchiveTypeConversionSupport.fromMultiScalarArchiveString(Vector.class, EpicsEnum.class, archiveString);
            Assert.assertEquals(2, enums.size());

            final Iterator<EpicsEnum> iterator = enums.iterator();
            final EpicsEnum first = iterator.next();
            Assert.assertEquals(Integer.valueOf(1), first.getRaw());

            final EpicsEnum second = iterator.next();
            Assert.assertEquals("second", second.getState());

        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    /**
     * For test purposes.
     *
     * @author bknerr
     * @since 13.12.2010
     */
    private static final class IDoNotExist {
        public IDoNotExist() {
            // EMPTY
        }
    }
    @Test(expected=TypeSupportException.class)
    public void testTypeNotSuppportedException() throws TypeSupportException {
        ArchiveTypeConversionSupport.toArchiveString(new IDoNotExist());
    }
    @Test(expected=TypeSupportException.class)
    public void testInvalidCollectionTypeConversions1() throws TypeSupportException {
        ArchiveTypeConversionSupport.fromMultiScalarArchiveString(LinkedList.class, Collection.class, "");
    }
    @Test(expected=TypeSupportException.class)
    public void testInvalidCollectionTypeConversions2() throws TypeSupportException {
        ArchiveTypeConversionSupport.fromArchiveString(Collection.class, "");
    }
}
