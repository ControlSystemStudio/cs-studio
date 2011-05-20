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
package org.csstudio.domain.desy;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Iterables;

/**
 * Test for {@link Strings}. 
 * 
 * @author bknerr
 * @since 17.05.2011
 */
public class StringsTest {
    
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
    }

    @Test
    public final void testSplitIgnoreInQuotesTrimmed() {
        Collection<String> split = 
            Strings.splitIgnoreWithinQuotesTrimmed("xxx  /tmp/demox\"Hello Dolly\"xthisxxxxisxxxx\" a test \"xxx", 'x', '\"');
        Iterator<String> it = split.iterator();
        assertEquals(5, split.size());
        assertEquals("  /tmp/demo", it.next());
        assertEquals("Hello Dolly", it.next());
        assertEquals("this", it.next());
        assertEquals("is", it.next());
        assertEquals(" a test ", it.next());
    }

    @Test
    public final void testSplitIgnoreInQuotesTrimmed2() {
        Collection<String> split = 
            Strings.splitIgnoreWithinQuotes("|||This is a|| || |\"complicated||test.\"|||Hello, \"fox|and\"dog", '|');
        Iterator<String> it = split.iterator();
        assertEquals(5, split.size());
        assertEquals("This is a", it.next());
        assertEquals(" ", it.next());
        assertEquals(" ", it.next());
        assertEquals("\"complicated||test.\"", it.next());
        assertEquals("Hello, \"fox|and\"dog", it.next());
    }
    
    @Test
    public final void testCreateListFromString() {
        
        Assert.assertNotNull(Strings.createListFrom(null));
        Assert.assertNotNull(Strings.createListFrom(""));
        Assert.assertNotNull(Strings.createListFrom("hallo"));

        Assert.assertEquals(0, Iterables.size(Strings.createListFrom(null)));
        Assert.assertEquals(0, Iterables.size(Strings.createListFrom("")));
        Assert.assertEquals(0, Iterables.size(Strings.createListFrom("  ")));
        Assert.assertEquals(0, Iterables.size(Strings.createListFrom(" ,  ")));
        Assert.assertEquals(1, Iterables.size(Strings.createListFrom("hallo")));
        Assert.assertEquals(1, Iterables.size(Strings.createListFrom("hallo ,")));

        Assert.assertEquals("hallo", Strings.createListFrom("hallo ,").iterator().next());
        Iterator<String> it = Strings.createListFrom(" , hallo , tut,").iterator();
        Assert.assertEquals("hallo", it.next());
        Assert.assertEquals("tut", it.next());
    }
    
    @Test
    public final void testSizeInBytes() {
        Assert.assertEquals(0, Strings.getSizeInBytes(null));
        Assert.assertEquals(0, Strings.getSizeInBytes(""));
        
        String normal1 = "Wir koennen spueren, wie wir die Form verlieren.";
        Assert.assertEquals(normal1.getBytes().length, Strings.getSizeInBytes(normal1));
        String normal2 = "1234567890!@#$%^&*()-_=+`~{}[]\\|'\";:,.<>/?";
        Assert.assertEquals(normal2.getBytes().length, Strings.getSizeInBytes(normal2));
        
        String freak = "€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";
        Assert.assertEquals(freak.getBytes().length, Strings.getSizeInBytes(freak));
    }
}
