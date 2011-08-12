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
package org.csstudio.domain.desy.types;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author bknerr
 * @since 27.01.2011
 */
public class BaseTypeConversionSupportHeadlessTest {

    @Test
    public void testCreateTypeClassForBasicTypes() throws TypeSupportException {

        Class<?> clazz = BaseTypeConversionSupport.createBaseTypeClassFromString(String.class.getSimpleName());
        Assert.assertTrue(clazz == String.class);
        clazz = BaseTypeConversionSupport.createBaseTypeClassFromString(ArrayList.class.getSimpleName());
        Assert.assertTrue(clazz == ArrayList.class);
        clazz = BaseTypeConversionSupport.createBaseTypeClassFromString(LinkedBlockingQueue.class.getSimpleName());
        Assert.assertTrue(clazz == LinkedBlockingQueue.class);
    }

    @Test
    public void testCreateTypeClassForGenericTypes() throws TypeSupportException {

        Class<?> al = BaseTypeConversionSupport.createBaseTypeClassFromString("ArrayList<Foo<Bar>>");
        Assert.assertTrue(al == ArrayList.class);

        al = BaseTypeConversionSupport.createBaseTypeClassFromString("  CopyOnWriteArrayList < Foo < Bar ,?.> >  ");
        Assert.assertTrue(al == CopyOnWriteArrayList.class);
    }

    @Test
    public void testCreateTypeClassFromStringFromBuddyPlugin() throws TypeSupportException {

        Assert.assertNotNull(BaseTypeConversionSupport.createBaseTypeClassFromString("EpicsEnum", "org.csstudio.domain.desy.epics.types"));
    }

    @Test(expected=TypeSupportException.class)
    public void testTypeSupportException() throws TypeSupportException {
        Assert.assertNull(BaseTypeConversionSupport.createBaseTypeClassFromString("Tralala", "org.csstudio.domain.desy.epics.types"));

    }

    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid1() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType("Slipped");
    }
    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid2() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType("Slipped<");
    }
    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid3() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType("<");
    }
    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid4() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType(">");
    }
    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid5() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType("<>");
    }
    @Test(expected=TypeSupportException.class)
    public void testFindFirstNestedTypeInvalid6() throws TypeSupportException {
        BaseTypeConversionSupport.parseForFirstNestedGenericType("<OH>");
    }

    @Test
    public void testFindFirstNestedType() throws TypeSupportException {
        String result = BaseTypeConversionSupport.parseForFirstNestedGenericType("Slipped<Dissolved>");
        Assert.assertEquals("Dissolved", result);
        result = BaseTypeConversionSupport.parseForFirstNestedGenericType("  Slipped <  Dissolved  <Loosed<?>>> ");
        Assert.assertEquals("Dissolved", result);
    }
}
