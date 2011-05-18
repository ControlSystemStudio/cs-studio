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
