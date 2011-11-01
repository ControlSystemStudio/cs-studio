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
package org.csstudio.archive.common.service.util;

import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_PREFIX;
import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.ARCHIVE_COLLECTION_SUFFIX;
import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.collectionEmbrace;
import static org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport.collectionRelease;

import java.io.Serializable;

import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test of scalar type conversion in {@link ArchiveTypeConversionSupport}.
 *
 * @author bknerr
 * @since 10.12.2010
 */
public class ArchiveTypeConversionSupportUnitTest {

    @BeforeClass
    public static void setup() {
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
    public void testDeSerialization() throws TypeSupportException {
        Serializable start = Double.valueOf(2.0);
        byte[] byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        Serializable result = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(start, result);

        start = "hello";
        byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        result = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(start, result);

        start = EpicsEnum.createFromRaw(1);
        byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        result = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(start, result);

        start = Lists.newArrayList(Double.valueOf(-1.0), Double.valueOf(2.0));
        byteArray = ArchiveTypeConversionSupport.toByteArray(start);
        result = ArchiveTypeConversionSupport.fromByteArray(byteArray);
        Assert.assertEquals(start, result);
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
            Assert.assertTrue("test me".equals(archiveString));
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
            // CHECKSTYLE OFF : NestedBlocks
                {
                    final EpicsEnum t = EpicsEnum.createFromRaw(3);
                    final String archiveString = ArchiveTypeConversionSupport.toArchiveString(t);
                    Assert.assertTrue(archiveString.equals(EpicsEnum.RAW + EpicsEnum.SEP + "3"));
                    final EpicsEnum tFromA = ArchiveTypeConversionSupport.fromArchiveString(EpicsEnum.class, archiveString);
                    Assert.assertNotNull(tFromA);
                    Assert.assertEquals(Integer.valueOf(3), tFromA.getRaw());
                }
                {
                    final EpicsEnum t = EpicsEnum.createFromStateName("MyState");
                    final String archiveString = ArchiveTypeConversionSupport.toArchiveString(t);
                    Assert.assertTrue(archiveString.equals(t.toString()));
                    final EpicsEnum tFromA = ArchiveTypeConversionSupport.fromArchiveString(EpicsEnum.class, archiveString);
                    Assert.assertNotNull(tFromA);
                    Assert.assertEquals("MyState", tFromA.getState());
                }
            // CHECKSTYLE ON : NestedBlocks
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
    private static final class IDoNotExist implements Serializable {
        private static final long serialVersionUID = 4206609653727732553L;

        public IDoNotExist() {
            // EMPTY
        }
    }


    @Test(expected=TypeSupportException.class)
    public void testTargetTypeDoesntExistException() throws TypeSupportException {
        ArchiveTypeConversionSupport.fromArchiveString("IDoNotExist", "Iwasborninafactory,,,,whohoo");
    }
    @Test(expected=TypeSupportException.class)
    public void testTypeNotSuppportedException() throws TypeSupportException {
        ArchiveTypeConversionSupport.toArchiveString(new IDoNotExist());
    }
}
