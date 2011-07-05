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
package org.csstudio.domain.desy.name;

import junit.framework.Assert;

import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.epics.name.EpicsNameSupport;
import org.csstudio.domain.desy.epics.name.IRecordField;
import org.csstudio.domain.desy.epics.name.RecordField;
import org.csstudio.domain.desy.epics.name.UnknownRecordField;
import org.junit.Test;

/**
 * Test for {@link EpicsNameSupport};
 *
 * @author bknerr
 * @since 24.06.2011O
 */
public class EpicsNameSupportUnitTest {

    @Test
    public void parseJustBaseName() {

        final String fullName = "he>l<L[0]_+-:;";
        final String baseName = EpicsNameSupport.parseBaseName(fullName);
        Assert.assertEquals(baseName, baseName);
    }

    @Test
    public void parseFullNameValid() {

        final String fullName = "he>l<L[0]_+-:;";
        final String baseName = EpicsNameSupport.parseBaseName(fullName + ".HIHI");
        Assert.assertEquals(baseName, baseName);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseTooLongBaseName() {
        final String tooLong = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM345678901";
        EpicsNameSupport.parseBaseName(tooLong);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseTooLongFieldName() {
        final String tooLong = "hello.ABCDE";
        EpicsNameSupport.parseBaseName(tooLong);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseTwoFieldSeps() {
        final String twoDots = "hello.RVAL.HIHI";
        EpicsNameSupport.parseBaseName(twoDots);
    }

    @Test
    public void parseForField() {

        IRecordField result = EpicsNameSupport.parseField("hello");
        Assert.assertEquals(RecordField.VAL.getFieldName(), result.getFieldName());


        for (final RecordField field : RecordField.values()) {
            result = EpicsNameSupport.parseField("hello" + EpicsChannelName.FIELD_SEP + field.getFieldName());
            Assert.assertEquals(field.getFieldName(), result.getFieldName());
        }

        result = EpicsNameSupport.parseField("hello.UNKN");
        Assert.assertTrue(result instanceof UnknownRecordField);
        Assert.assertEquals("UNKN", result.getFieldName());


    }

}
