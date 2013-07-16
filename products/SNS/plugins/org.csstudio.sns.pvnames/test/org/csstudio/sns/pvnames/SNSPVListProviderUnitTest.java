/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.pvnames;

import static org.csstudio.utility.test.HamcrestMatchers.greaterThan;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.junit.Test;

/** JUnit test of the {@link SNSPVListProvider}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProviderUnitTest
{
    @Test
    public void locatePVName()
    {
        final IAutoCompleteProvider provider = new SNSPVListProvider();
        AutoCompleteResult pvs = provider.listResult("PV", "DTL_LLRF:IOC1:L*", 10);
        System.out.println("Matching PVs: " + pvs.getCount());
        System.out.println(pvs.getProposalsAsString());
        assertThat(pvs.getCount(), greaterThan(1));
        assertThat(pvs.getProposalsAsString().contains("DTL_LLRF:IOC1:Load"), equalTo(true));
    }
}
