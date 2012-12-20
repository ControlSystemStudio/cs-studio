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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.03.2011
 */
//CHECKSTYLE:OFF
public class ParsedGsdFileModelUnitTest {
    
    private ParsedGsdFileModel _out;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        final GSDFileDBO gsdFileDBO = new GSDFileDBO("JUnitTest", "#Profibus_DP\nVendor_Name            = JUnitTest");
        _out = new ParsedGsdFileModel(gsdFileDBO);
    }
    
    @Test
    public void testProperty() throws Exception {
        final String value1 = "\"Das ist ein Value\"";
        final String value2 = "\"aAbBcC!§$%&/()=?+#*',.-;:_\"";
        final String value3 = "\"1234\"";
        final List<Integer> listValue1 = Arrays.asList(0,16,171,161,93);
        final List<Integer> listValue2 = Arrays.asList(0,16,123,12,99);
        final List<Integer> listValue3 = Arrays.asList(0,16,123,12,171);
        
        KeyValuePair keyValuePair = new KeyValuePair("int1", "123456");
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("int2", "0xAB");
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("int3", "0x00");
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("string1", value1);
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("string2", value2);
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("string3", value3);
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("intList1", "0x00,0x10, 0xAB, 0xa1, 0x5D");
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("intList2", "0,16, 123, 012, 99");
        _out.setProperty(keyValuePair);
        keyValuePair = new KeyValuePair("intList3", "0x00,0x10, 123, 012, 0xAb");
        _out.setProperty(keyValuePair);
        testIntegerValue();
        testStringVaule(value1, value2, value3);
        testIntListValues(listValue1, listValue2, listValue3);
    }

    private void testIntListValues(@Nonnull final List<Integer> listValue1,
                                   @Nonnull final List<Integer> listValue2,
                                   @Nonnull final List<Integer> listValue3) {
        // Test Integer List values
        Assert.assertEquals(listValue1, _out.getIntListValue("intList1"));
        Assert.assertEquals(listValue2, _out.getIntListValue("intList2"));
        Assert.assertEquals(listValue3, _out.getIntListValue("intList3"));
        // Test wrong Type
        Assert.assertNull(_out.getIntListValue("int3"));
        Assert.assertNull(_out.getIntListValue("string3"));
        Assert.assertNull(_out.getIntListValue("unknownProperty"));
    }

    private void testStringVaule(@Nonnull final String value1, @Nonnull final String value2, @Nonnull final String value3) {
        // Test String values
        Assert.assertEquals(value1, _out.getStringValue("string1"));
        Assert.assertEquals(value2, _out.getStringValue("string2"));
        Assert.assertEquals(value3, _out.getStringValue("string3"));
        // Test wrong Type
        Assert.assertNull(_out.getStringValue("int2"));
        Assert.assertNull(_out.getStringValue("intList2"));
        Assert.assertNull(_out.getStringValue("unknownProperty"));
    }

    private void testIntegerValue() {
        // Test Integer values
        Assert.assertEquals(Integer.valueOf(123456), _out.getIntValue("int1"));
        Assert.assertEquals(Integer.valueOf(171), _out.getIntValue("int2"));
        Assert.assertEquals(Integer.valueOf(0), _out.getIntValue("int3"));
        // Test wrong Type
        Assert.assertNull(_out.getIntValue("sting1"));
        Assert.assertNull(_out.getIntValue("intList1"));
        Assert.assertNull(_out.getIntValue("unknownProperty"));
    }
    
    @Test
    public void testPutModel() throws Exception {
        final List<Integer> value1 = Arrays.asList(0,16,171,161,93);
        final GsdModuleModel2 gsdModuleModel1 = new GsdModuleModel2("M1", value1);
        gsdModuleModel1.setModuleNumber(1);
        _out.setModule(gsdModuleModel1);
        final GsdModuleModel2 gsdModuleModel2 = new GsdModuleModel2("M2", value1);
        gsdModuleModel2.setModuleNumber(2);
        _out.setModule(gsdModuleModel2);
        final GsdModuleModel2 gsdModuleModel3 = new GsdModuleModel2("M3", value1);
        gsdModuleModel3.setModuleNumber(3);
        _out.setModule(gsdModuleModel3);
        
        Assert.assertEquals(gsdModuleModel1, _out.getModule(1));
        Assert.assertEquals(gsdModuleModel2, _out.getModule(2));
        Assert.assertEquals(gsdModuleModel3, _out.getModule(3));
        Assert.assertNull(_out.getModule(4));
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testPutModelFail() throws Exception {
        final List<Integer> value1 = Arrays.asList(0,16,171,161,93);
        final GsdModuleModel2 gsdModuleModel1 = new GsdModuleModel2("M1", value1);
        gsdModuleModel1.setModuleNumber(1);
        _out.setModule(gsdModuleModel1);
        final GsdModuleModel2 gsdModuleModel2 = new GsdModuleModel2("M2", value1);
        gsdModuleModel1.setModuleNumber(2);
        _out.setModule(gsdModuleModel2);
        
        final GsdModuleModel2 gsdModuleModel3 = new GsdModuleModel2("M3", value1);
        // throws exeption! Each ModuleNumber only once per file!
        gsdModuleModel1.setModuleNumber(1);
        _out.setModule(gsdModuleModel3);
    }
}
//CHECKSTYLE:ON
