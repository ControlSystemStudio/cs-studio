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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.cosyrules.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: $
 * @version $Revision: 1.7 $
 * @since 17.09.2010
 */
public class AlarmBorderTest {

    private AlarmBorder _alarmBorder;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _alarmBorder = new AlarmBorder();

    }

    @Test
    public void longArgument() throws Exception {
        Object[] out;
        Object evaluate;

        out = new Object[] { -1l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.DOTTED.getIndex(), evaluate);

        out = new Object[] { 0l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { 1l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 2l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 3l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);
    }

    @Test
    public void doubleArgument() throws Exception {
        Object[] out;
        Object evaluate;

        out = new Object[] { -1d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.DOTTED.getIndex(), evaluate);

        out = new Object[] { 0.000000001d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { 0.99999999999999d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 2.d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 3.d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);
    }

    @Test
    public void stringArgument() throws Exception {
        Object[] out;
        Object evaluate;

        out = new Object[] { "yXxcGDS" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.DOTTED.getIndex(), evaluate);

        out = new Object[] { "NORMAL" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { "WARNING" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { "ALARM" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { "ERROR" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);
    }

    @Test
    public void dynamicValueStateArgument() throws Exception {
        Object[] out;
        Object evaluate;

        out = new Object[] { new DynamicValueCondition(DynamicValueState.NO_VALUE) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.DOTTED.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.NORMAL) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.WARNING) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.ALARM) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.ERROR) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);
    }

    @Test
    public void miscArgument() throws Exception {
        Object[] out;
        Object evaluate;

        out = new Object[] { 0l, 0.d, "NORMAL" };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { 1.d, "WARNING", 1l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.NORMAL) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.NONE.getIndex(), evaluate);

        out = new Object[] { "ALARM", 2l, 2.d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.ALARM) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 0.d, "WARNING", 0l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 0.d, "NORMAL", 1l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 0.d, "ERROR", 1l };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { new DynamicValueCondition(DynamicValueState.WARNING) };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);

        out = new Object[] { 1.d, "NORMAL", 0l, 0.d, "NORMAL", 1l, 1.d, "WARNING", 0l, "NORMAL",
                0.d };
        evaluate = _alarmBorder.evaluate(out);
        assertEquals(BorderStyleEnum.LINE.getIndex(), evaluate);
    }

    @Test
    public void testDescription() {
        final String description = _alarmBorder.getDescription();
        assertNotNull(description);
        assertTrue(description.length() > 0);
    }

}
