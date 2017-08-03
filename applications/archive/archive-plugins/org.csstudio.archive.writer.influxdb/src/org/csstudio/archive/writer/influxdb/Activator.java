/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.influxdb;

import java.util.logging.Logger;

/** Not really an Activator, just holds related info and could turn into activator
 *  @author Megan Grodowitz
 *
 */
@SuppressWarnings("nls")
public class Activator
{
    /** Plugin ID defined in MANIFEST.MF */
    public static final String ID = "org.csstudio.archive.writer.influxdb";

    /** @return Logger for the plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }
}
