/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.engineconfig.RetentionHelper;
import org.csstudio.archive.rdb.internal.SQL;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.Test;

/** Retention tests
 *  <p>
 *  Assumes that there is already a manually created entry 9999/Forever
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RetentionHelperTest
{
    @Test
    public void test() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(TestSetup.URL, false);
        final SQL sql = new SQL(rdb.getDialect(), false);

        final RetentionHelper retentions = new RetentionHelper(rdb, sql);

        Retention retention = retentions.getRetention("Forever");
        System.out.println(retention);
        assertEquals(9999, retention.getId());
        System.out.println(retentions.getRetention("Long time"));
        System.out.println(retentions.getRetention("temporary"));

        retentions.dispose();

        rdb.close();
    }

}
