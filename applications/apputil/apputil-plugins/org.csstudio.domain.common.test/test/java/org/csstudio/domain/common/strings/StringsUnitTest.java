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
package org.csstudio.domain.common.strings;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for {@link Strings}.
 *
 * @author bknerr
 * @since 17.05.2011
 */
public class StringsUnitTest {

    @Test
    public final void testTrim() {
        Assert.assertEquals("", Strings.trim("", 'f'));
        Assert.assertEquals("", Strings.trim("x", 'x'));
        Assert.assertEquals("ha ha", Strings.trim("  ha ha  ", ' '));
        Assert.assertEquals("h\ta \tha", Strings.trim("\t\th\ta \tha\t", '\t'));
        Assert.assertEquals(" a\"h\"a ", Strings.trim("\"\" a\"h\"a ", '\"'));
        Assert.assertEquals("aha ", Strings.trim("aha \"\"", '\"'));
        Assert.assertEquals("a??=[]()//", Strings.trim("|||a??=[]()//|", '|'));
    }

    @Test
    public final void testSplitIgnoreInQuotesEmptyReturns() {

        Collection<String> split = Strings.splitIgnoreWithinQuotes("", ' ');
        assertEquals(0, split.size());
        split = Strings.splitIgnoreWithinQuotes("x", 'x');
        assertEquals(0, split.size());
        split = Strings.splitIgnoreWithinQuotes("   ", ' ');
        assertEquals(0, split.size());

    }

    @Test
    public final void testSplitIgnoreInQuotes() {

        Collection<String> split = Strings.splitIgnoreWithinQuotes("tritra  tru lala", ' ');
        assertEquals(3, split.size());
        Iterator<String> it = split.iterator();
        assertEquals("tritra", it.next());
        assertEquals("tru", it.next());
        assertEquals("lala", it.next());

        split = Strings.splitIgnoreWithinQuotes("  tri\"tra tru \" lala", ' ');
        it = split.iterator();
        assertEquals(2, split.size());
        assertEquals("tri\"tra tru \"", it.next());
        assertEquals("lala", it.next());

        split = Strings.splitIgnoreWithinQuotes("aa,\"bb,bb\",cc\",dd\"ee", ',');
        it = split.iterator();
        assertEquals(3, split.size());
        assertEquals("aa", it.next());
        assertEquals("\"bb,bb\"", it.next());
        assertEquals("cc\",dd\"ee", it.next());


        split = Strings.splitIgnoreWithinQuotes("xxx  /tmp/demox\"Hello Dolly\"xthisxxxxisxxxx\" a test \"xxx", 'x');
        it = split.iterator();
        assertEquals(5, split.size());
        assertEquals("  /tmp/demo", it.next());
        assertEquals("\"Hello Dolly\"", it.next());
        assertEquals("this", it.next());
        assertEquals("is", it.next());
        assertEquals("\" a test \"", it.next());

    }

    @Test
    public final void testSplitIgnore() {
        Collection<String> split =
            Strings.splitIgnore("xxx  /tmp/demox\"Hello Dolly\"xthisxxxxisxxxx\" a test \"xxx",
                                'x',
                                'i');
        Iterator<String> it = split.iterator();
        assertEquals(4, split.size());
        assertEquals("  /tmp/demo", it.next());
        assertEquals("\"Hello Dolly\"", it.next());
        assertEquals("thisxxxxis", it.next());
        assertEquals("\" a test \"", it.next());

        split = Strings.splitIgnore("aa,|bb,bb|,cc|,ddee", ',', '|');
        it = split.iterator();
        assertEquals(4, split.size());
        assertEquals("aa", it.next());
        assertEquals("|bb,bb|", it.next());
        assertEquals("cc|", it.next());
        assertEquals("ddee", it.next());
    }

    @Test
    public final void testSplitIgnoreInQuotesTrimmed() {
        final Collection<String> split =
            Strings.splitIgnoreWithinQuotesTrimmed("xxx  /tmp/demox\"Hello Dolly\"xthisxxxxisxxxx\" a test \"xxx", 'x', '\"');
        final Iterator<String> it = split.iterator();
        assertEquals(5, split.size());
        assertEquals("  /tmp/demo", it.next());
        assertEquals("Hello Dolly", it.next());
        assertEquals("this", it.next());
        assertEquals("is", it.next());
        assertEquals(" a test ", it.next());
    }

    @Test
    public final void testSplitIgnoreInQuotesTrimmedRegexLiteral() {
        final Collection<String> split =
            Strings.splitIgnoreWithinQuotes("|||This is a|| || |\"complicated||test.\"|||Hello, \"fox|and\"dog", '|');
        final Iterator<String> it = split.iterator();
        assertEquals(5, split.size());
        assertEquals("This is a", it.next());
        assertEquals(" ", it.next());
        assertEquals(" ", it.next());
        assertEquals("\"complicated||test.\"", it.next());
        assertEquals("Hello, \"fox|and\"dog", it.next());
    }

    @Test
    public final void testSplitOnCommasIgnoreInQuotes() {

        final String[] empty = Strings.splitOnCommaIgnoreInQuotes("");
        Assert.assertNotNull(empty);
        Assert.assertEquals(1, empty.length);
        Assert.assertEquals("", empty[0]);

        final String[] notEmpty = Strings.splitOnCommaIgnoreInQuotes("hallo");
        Assert.assertNotNull(notEmpty);
        Assert.assertEquals(1, notEmpty.length);
        Assert.assertEquals("hallo", notEmpty[0]);

        Assert.assertEquals(1, Strings.splitOnCommaIgnoreInQuotes("  ").length);
        Assert.assertEquals(2, Strings.splitOnCommaIgnoreInQuotes(" ,  ").length);

        String[] source = Strings.splitOnCommaIgnoreInQuotes("a,b,\"c,d\",e");
        Assert.assertEquals(4, source.length);
        Assert.assertEquals("a", source[0]);
        Assert.assertEquals("b", source[1]);
        Assert.assertEquals("\"c,d\"", source[2]);
        Assert.assertEquals("e", source[3]);

        source = Strings.splitOnCommaIgnoreInQuotes("conf,\"use");
        Assert.assertEquals(1, source.length);
        Assert.assertEquals("conf,\"use", source[0]);

        source = Strings.splitOnCommaIgnoreInQuotes("co,\"nf,\"us,\"e");
        Assert.assertEquals(2, source.length);
        Assert.assertEquals("co,\"nf", source[0]);
        Assert.assertEquals("\"us,\"e", source[1]);


    }

    @Test
    public final void testSizeInBytes() {
        Assert.assertEquals(0, Strings.getSizeInBytes(""));

        final String normal1 = "Wir koennen spueren, wie wir die Form verlieren.";
        Assert.assertEquals(normal1.getBytes().length, Strings.getSizeInBytes(normal1));
        final String normal2 = "1234567890!@#$%^&*()-_=+`~{}[]\\|'\";:,.<>/?";
        Assert.assertEquals(normal2.getBytes().length, Strings.getSizeInBytes(normal2));

        final String freak = "€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";
        Assert.assertEquals(freak.getBytes().length, Strings.getSizeInBytes(freak));
    }
}
