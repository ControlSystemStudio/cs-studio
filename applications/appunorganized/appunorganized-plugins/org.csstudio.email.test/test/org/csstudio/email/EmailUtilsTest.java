/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.email;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Test for the email utilities.
 *
 * <local-part@subdomain.domain.tld>
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 06.04.2010
 */
@SuppressWarnings("nls")
public class EmailUtilsTest {

    private static String[] ADDRESSES = {
      "fetz.braun@knax.de",
      "!#$%&'*+-/=^_{}~@a-zA-Z0-9.de",
      "a@b.c",
      "a@b.c.d.e"
    };

    private static String[] INVALID_ADDRESSES = {
        " x@x ",
        " x@x.xx",
        " a@!.de "
        //," a@1234567890123456789012345678901234567890123456789012345678901234.de" // domain.length > 63
      };



    private static String TEXT_WITH_VALID_ADDRESSES =
      "Trit@ratr@ullala," + ADDRESSES[0] + " " +
      ADDRESSES[1] + ";ölascnkj;" + ADDRESSES[2] + "!" +
      ADDRESSES[3] + "\\akslfcnhkjh" +
      INVALID_ADDRESSES[0] +
      INVALID_ADDRESSES[1] +
      INVALID_ADDRESSES[2]
                        //+ INVALID_ADDRESSES[3]
      ;

    @Test
    public void testAddresses() {
        final Set<String> addresses = EmailUtils.extractEmailAddresses(TEXT_WITH_VALID_ADDRESSES);
        Assert.assertEquals(ADDRESSES.length, addresses.size());
        for (final String address : addresses) {
            Assert.assertTrue(addresses.contains(address));
        }
    }


}
