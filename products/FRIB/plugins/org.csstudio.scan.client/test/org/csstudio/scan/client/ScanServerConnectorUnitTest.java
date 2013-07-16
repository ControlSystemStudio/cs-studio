/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
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
package org.csstudio.scan.client;
import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.server.ScanServer;
import org.junit.Test;

/** JUnit test of the {@link ScanServerConnector}
 *  @author Kay Kasemir
 */
public class ScanServerConnectorUnitTest
{
    @Test
    public void testConnector() throws Exception
    {
        final ScanServer server = ScanServerConnector.connect();
        System.out.println(server.getInfo());
        ScanServerConnector.disconnect(server);
    }
}
