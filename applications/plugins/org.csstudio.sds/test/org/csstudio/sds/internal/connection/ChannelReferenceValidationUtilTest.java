/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 /**
 *
 */
package org.csstudio.sds.internal.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChannelReferenceValidationUtil}.
 *
 * @author swende
 *
 */
public final class ChannelReferenceValidationUtilTest {
    /**
     * Some aliases.
     */
    private HashMap<String, String> _aliases;

    /**
     * A counter for validation exceptions.
     */
    private int _validationExceptionsCount;

    /**
     * Setup.
     */
    @Before
    public void setUp() {
        _validationExceptionsCount = 0;

        _aliases = new HashMap<String, String>();
        // a-d: normal aliases
        _aliases.put("a", "<a>$b$</a>");
        _aliases.put("b", "<b>$c$</b>");
        _aliases.put("c", "<c>$d$</c>");
        _aliases.put("d", "<d />");
        // e-f: produce a circular relationship
        _aliases.put("e", "<e>$f$</e>");
        _aliases.put("f", "<f>$e$</f>");
        // g: references a alias, which is not available
        _aliases.put("g", "<e>$Missing$</e>");

    }

    /**
     * Test method for
     * {@link ChannelReferenceValidationUtil#createCanonicalName(String, HashMap)}.
     */
    @Test
    public void testGetFullQualifiedName() {

        try {
            // test text which contains no alias
            assertEquals(ChannelReferenceValidationUtil.createCanonicalName(
                    "abcdefg", _aliases), "abcdefg");

            // test text which contains aliases that can be processed
            assertEquals(ChannelReferenceValidationUtil.createCanonicalName(
                    "$a$", _aliases), "<a><b><c><d /></c></b></a>");
            assertEquals(ChannelReferenceValidationUtil.createCanonicalName(
                    "$b$", _aliases), "<b><c><d /></c></b>");
            assertEquals(ChannelReferenceValidationUtil.createCanonicalName(
                    "$c$", _aliases), "<c><d /></c>");
            assertEquals(ChannelReferenceValidationUtil.createCanonicalName(
                    "$d$", _aliases), "<d />");

            // test text which contains aliases that can produce a circular
            // relationship
            assertTrue(_validationExceptionsCount == 0);
            ChannelReferenceValidationUtil.createCanonicalName("$e$", _aliases);
            assertTrue(_validationExceptionsCount == 1);

            // test text which contains aliases that are not defined
            assertTrue(_validationExceptionsCount == 1);
            ChannelReferenceValidationUtil.createCanonicalName("$g$", _aliases);
            assertTrue(_validationExceptionsCount == 2);

        } catch (ChannelReferenceValidationException e) {
            _validationExceptionsCount++;
        }

    }

    /**
     * Test method for
     * {@link ChannelReferenceValidationUtil#testValidity(String)}.
     */
    @Test
    public void testValidity() {
        // inputs with correct syntax
        assertTrue(ChannelReferenceValidationUtil.testValidity("abc"));
        assertTrue(ChannelReferenceValidationUtil.testValidity("$a$$b$"));
        assertTrue(ChannelReferenceValidationUtil.testValidity("xxx$a$$b$"));
        assertTrue(ChannelReferenceValidationUtil.testValidity("$a$$b$xxx"));
        assertTrue(ChannelReferenceValidationUtil.testValidity("xxx$a$$b$xxx"));
        assertTrue(ChannelReferenceValidationUtil
                .testValidity("xxx$a$xxx$b$xxx"));

        // inputs with incorrect syntax
        assertFalse(ChannelReferenceValidationUtil.testValidity("$"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$xxx"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$a$xxx$"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$a$xxx$xx"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("$$"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$$"));
        assertFalse(ChannelReferenceValidationUtil.testValidity("xxx$$xxx"));
    }
}
