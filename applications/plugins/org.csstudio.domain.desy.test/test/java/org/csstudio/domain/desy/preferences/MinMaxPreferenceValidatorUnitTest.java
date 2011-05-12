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
package org.csstudio.domain.desy.preferences;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for {@link MinMaxPreferenceValidator}. 
 * 
 * @author bknerr
 * @since 11.05.2011
 */
public class MinMaxPreferenceValidatorUnitTest {
    
    

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidValidator() {
        @SuppressWarnings("unused")
        final MinMaxPreferenceValidator<Byte> val = 
            new MinMaxPreferenceValidator<Byte>(Byte.valueOf((byte)0), Byte.valueOf((byte)-1));
    }

    @Test
    public void test() {
        final MinMaxPreferenceValidator<Byte> val = 
            new MinMaxPreferenceValidator<Byte>(Byte.valueOf((byte) -0), Byte.valueOf((byte) 10));
        Assert.assertTrue(val.validate((byte) 0));
        Assert.assertTrue(val.validate((byte) 5));
        Assert.assertTrue(val.validate((byte) 10));
        Assert.assertFalse(val.validate((byte) -1));
        Assert.assertFalse(val.validate((byte) 11));
    }
}
