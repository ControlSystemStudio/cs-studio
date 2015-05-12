/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.remote.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class CommandResultTest {

    @Test
    public void testSuccessWithoutReturnValue() throws Exception {
        CommandResult r = CommandResult.createSuccessResult();
        assertNull(r.getValue());
        assertEquals(CommandResult.TYPE_VOID, r.getType());
    }

    @Test
    public void testSuccessWithReturnValue() throws Exception {
        CommandResult r = CommandResult.createSuccessResult("foo", "test");
        assertEquals("foo", r.getValue());
        assertEquals("test", r.getType());
    }

    @Test
    public void testMessage() throws Exception {
        CommandResult r = CommandResult.createMessageResult("foo");
        assertEquals("foo", r.getValue());
        assertEquals(CommandResult.TYPE_MESSAGE, r.getType());
    }

    @Test
    public void testFailureWithoutMessageOrException() throws Exception {
        CommandResult r = CommandResult.createFailureResult();
        assertNull(r.getValue());
        assertEquals(CommandResult.TYPE_ERROR, r.getType());
    }

    @Test
    public void testFailureWithErrorMessage() throws Exception {
        CommandResult r = CommandResult.createFailureResult("message");
        assertEquals("message", r.getValue());
        assertEquals(CommandResult.TYPE_ERROR_MESSAGE, r.getType());
    }

    @Test
    public void testFailureWithException() throws Exception {
        Exception e = new RuntimeException();
        CommandResult r = CommandResult.createFailureResult(e);
        assertEquals(e, r.getValue());
        assertEquals(CommandResult.TYPE_EXCEPTION, r.getType());
    }

    @Test
    public void testToStringReturnsAString() throws Exception {
        CommandResult r = CommandResult.createSuccessResult();
        assertNotNull(r.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidReturnValue() throws Exception {
        CommandResult.createSuccessResult(null, CommandResult.TYPE_VOID);
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidReturnType() throws Exception {
        CommandResult.createSuccessResult("foo", null);
    }
}
