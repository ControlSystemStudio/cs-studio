/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.pvnames;

import java.util.regex.Pattern;

import org.csstudio.pvnames.IPVListProvider;
import org.csstudio.pvnames.PVListResult;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.csstudio.utility.test.HamcrestMatchers.*;

/** JUnit test of the {@link SNSPVListProvider}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProviderUnitTest
{
    @Test
    public void locatePVName()
    {
        final IPVListProvider provider = new SNSPVListProvider();
        PVListResult pvs = provider.listPVs(Pattern.compile("DTL_LLRF:IOC1:L.*"), 10);
        System.out.println("Matching PVs: " + pvs.getCount());
        System.out.println(pvs.getPvs());
        assertThat(pvs.getPvs().size(), greaterThan(1));
        assertThat(pvs.getPvs().contains("DTL_LLRF:IOC1:Load"), equalTo(true));
    }
}
