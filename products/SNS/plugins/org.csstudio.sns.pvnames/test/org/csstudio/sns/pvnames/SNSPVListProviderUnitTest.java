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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.junit.Test;


/** JUnit test of the {@link SNSPVListProvider}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProviderUnitTest
{
    @Test
    public void showSettings()
    {
        System.out.println("URL: " + Preferences.getURL());
        System.out.println("User: " + Preferences.getUser());
        System.out.println("PW: " + Preferences.getPassword().length() + " chars");
    }
    
    @Test
    public void locatePVName()
    {
        Logger logger = Logger.getLogger("");
        Level level = Level.ALL;
        logger.setLevel(level);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(level);
        Logger.getLogger("javax").setLevel(Level.WARNING);
        
        final IAutoCompleteProvider provider = new SNSPVListProvider();
		ContentDescriptor cd = new ContentDescriptor();
		cd.setAutoCompleteType(AutoCompleteType.PV);
		cd.setValue("DTL_LLRF:IOC1:L*");
		AutoCompleteResult pvs = provider.listResult(cd, 10);
        System.out.println("Matching PVs: " + pvs.getCount());
        System.out.println(pvs.getProposalsAsString());
        assertThat(pvs.getCount(), greaterThan(1));
        assertThat(pvs.getProposalsAsString().contains("DTL_LLRF:IOC1:Load"), equalTo(true));
    }
}
