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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.internal.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;


/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 22.06.2011
 */
public class AliasRuleJunitTest {
    
    @Test
    public void testEvaluate() throws Exception {
        AliasRule aliasRule = new AliasRule();
        assertNotNull(aliasRule.getDescription());
        assertTrue(aliasRule.getDescription().length()>0);
        
        Object out = aliasRule.evaluate(new String[] {"MyChannel"});
        assertTrue(out instanceof Map);
        Map outMap = (Map) out;
        String outChannel = (String) outMap.get("channel");
        assertNotNull(outChannel);
        assertEquals("MyChannel", outChannel);
        
        out = aliasRule.evaluate(new String[] {"MyChannel[AlarmMax]"});
        assertTrue(out instanceof Map);
        outMap = (Map) out;
        outChannel = (String) outMap.get("channel");
        assertNotNull(outChannel);
        assertEquals("MyChannel", outChannel);

        out = aliasRule.evaluate(new String[] {"MyChannel.EGU"});
        assertTrue(out instanceof Map);
        outMap = (Map) out;
        outChannel = (String) outMap.get("channel");
        assertNotNull(outChannel);
        assertEquals("MyChannel", outChannel);

        out = aliasRule.evaluate(new String[] {"MyChannel MNS PP"});
        assertTrue(out instanceof Map);
        outMap = (Map) out;
        outChannel = (String) outMap.get("channel");
        assertNotNull(outChannel);
        assertEquals("MyChannel", outChannel);
        
        out = aliasRule.evaluate(new String[] {"MyChannel.EGU MNS PP"});
        assertTrue(out instanceof Map);
        outMap = (Map) out;
        outChannel = (String) outMap.get("channel");
        assertNotNull(outChannel);
        assertEquals("MyChannel", outChannel);
    }
}
