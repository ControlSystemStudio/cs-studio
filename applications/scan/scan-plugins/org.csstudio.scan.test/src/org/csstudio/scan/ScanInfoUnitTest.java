/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Optional;

import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.junit.Test;

/** JUnit test of the ScanInfo formatting
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoUnitTest
{
    @Test
    public void testScanInfo()
    {
        final long runtime_ms = 1000l * (123 * 60*60 + 4*60 + 5);
        final ScanInfo info = new ScanInfo(new Scan(42, "test", Instant.now()), ScanState.Running, Optional.of("Hello"), runtime_ms, 0, 5, 10, 4, "SomeCommand");

        System.out.println(info);
        assertEquals(50, info.getPercentage());

        System.out.println("Runtime: " + info.getRuntimeText());
        assertEquals("123:04:05", info.getRuntimeText());
    }
}
