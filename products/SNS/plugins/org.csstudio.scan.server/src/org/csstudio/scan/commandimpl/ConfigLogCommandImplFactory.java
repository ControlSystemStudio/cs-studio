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
package org.csstudio.scan.commandimpl;

import org.csstudio.scan.command.ConfigLogCommand;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplFactory;

/** Factory for creating the implementation of a {@link ConfigLogCommand}
 *  @author Kay Kasemir
 */
public class ConfigLogCommandImplFactory implements ScanCommandImplFactory<ConfigLogCommand>
{
    @Override
    public ScanCommandImpl<ConfigLogCommand> createImplementation(final ConfigLogCommand command)
            throws Exception
    {
        return new ConfigLogCommandImpl(command);
    }
}
