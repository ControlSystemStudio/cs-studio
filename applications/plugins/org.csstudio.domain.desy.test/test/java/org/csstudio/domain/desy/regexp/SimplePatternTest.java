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
package org.csstudio.domain.desy.regexp;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since Mar 18, 2011
 */
public class SimplePatternTest {

    @Test(expected=PatternSyntaxException.class)
    public void testBackSlashesContained() {
        SimplePattern.toRegExp("wowee\\zowee");
    }

    @Test
    public void testNormalInput() {
        final String regExp = SimplePattern.toRegExp("*tru*llal?l?");
        final Pattern pattern = Pattern.compile(regExp);

        Assert.assertTrue(pattern.matcher("fup_trullalulu").matches());
        Assert.assertTrue(pattern.matcher("trullalala").matches());
        Assert.assertTrue(pattern.matcher("truuuullal?l2").matches());


        Assert.assertFalse(pattern.matcher("trullalalalaksjvksjnhvksdjh").matches());
        Assert.assertFalse(pattern.matcher("trullalaalaa").matches());
        Assert.assertFalse(pattern.matcher("trullallal").matches());
    }

    @Test
    public void testFancyInput() {
        final String regExp = SimplePattern.toRegExp("*tru*llal?l?");
        final Pattern pattern = Pattern.compile(regExp);
        Assert.assertTrue(pattern.matcher("!#$%&'()+,-./:;<=>@[]^_`{|}~truuullalala").matches());

    }
}
