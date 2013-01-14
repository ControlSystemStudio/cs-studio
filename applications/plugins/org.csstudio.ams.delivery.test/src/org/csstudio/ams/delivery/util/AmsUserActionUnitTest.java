
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.delivery.util;

import junit.framework.Assert;

import org.csstudio.ams.delivery.action.AmsUserAction;
import org.junit.Test;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 03.01.2012
 */
public class AmsUserActionUnitTest {
    
    @Test
    public final void testAmsUserAction() {
    
        AmsUserAction out = new AmsUserAction(null);
        Assert.assertFalse(out.hasValidFormat());
        
        out = new AmsUserAction("         ");
        Assert.assertFalse(out.hasValidFormat());
        
        out = new AmsUserAction(" 1234 ");
        Assert.assertFalse(out.hasValidFormat());

        out = new AmsUserAction("1 * 2 * 3 * 4 * 5 * 6 * 7");
        Assert.assertFalse(out.hasValidFormat());

        out = new AmsUserAction("1234*DerCode");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertTrue(out.isReplyAlarmAction());
        
        out = new AmsUserAction("#* .adg");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertEquals("#", out.getChainIdAsString());
        Assert.assertEquals("11234", out.getConfirmCode());
        
        out = new AmsUserAction("G*1*2*0*");
        Assert.assertFalse(out.hasValidFormat());

        out = new AmsUserAction("G*1*2*0*1234");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertTrue(out.isChangeGroupAction());
        Assert.assertEquals(1, out.getGroupId());
        Assert.assertEquals(2, out.getUserId());
        Assert.assertEquals(0, out.getStatus());
        Assert.assertEquals("1234", out.getConfirmCode());
        
        out = new AmsUserAction("1*2*1*5432");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertFalse(out.isChangeGroupAction());
        Assert.assertTrue(out.isChangeUserAction());
        Assert.assertEquals(1, out.getGroupId());
        Assert.assertEquals(2, out.getUserId());
        Assert.assertEquals(1, out.getStatus());
        Assert.assertEquals("5432", out.getConfirmCode());
        
        out = new AmsUserAction("*1*2*0*1234");
        Assert.assertFalse(out.hasValidFormat());
        
        out = new AmsUserAction("G*1*2*0*1234*HoleriDuDoedelDu");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertTrue(out.isChangeGroupAction());
        Assert.assertEquals(1, out.getGroupId());
        Assert.assertEquals(2, out.getUserId());
        Assert.assertEquals(0, out.getStatus());
        Assert.assertEquals("1234", out.getConfirmCode());
        Assert.assertEquals("HoleriDuDoedelDu", out.getReason());
        
        out = new AmsUserAction("1*2*1*5432*HoleriDuDoedelDu");
        Assert.assertTrue(out.hasValidFormat());
        Assert.assertFalse(out.isChangeGroupAction());
        Assert.assertTrue(out.isChangeUserAction());
        Assert.assertEquals(1, out.getGroupId());
        Assert.assertEquals(2, out.getUserId());
        Assert.assertEquals(1, out.getStatus());
        Assert.assertEquals("5432", out.getConfirmCode());
        Assert.assertEquals("HoleriDuDoedelDu", out.getReason());
    }
}
