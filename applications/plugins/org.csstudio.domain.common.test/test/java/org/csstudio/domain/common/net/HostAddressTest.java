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
package org.csstudio.domain.common.net;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for {@link HostAddress}.
 *
 * @author bknerr
 * @since 26.04.2011
 */
@SuppressWarnings("unused")
public class HostAddressTest {

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHostAddress1() {
        new HostAddress("");
    }
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHostAddress2() {
        new HostAddress(".");
    }
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidHostAddress3() {
        new HostAddress("_._");
    }

    @Test
    public void validHostAddress1() {
        final HostAddress a = new HostAddress("foo");
        Assert.assertEquals("foo", a.getHostAddress());
    }

    @Test
    public void validHostAddress2() {
        final HostAddress a = new HostAddress("www.haldern-pop.de");
        Assert.assertEquals("www.haldern-pop.de", a.getHostAddress());
    }
}
