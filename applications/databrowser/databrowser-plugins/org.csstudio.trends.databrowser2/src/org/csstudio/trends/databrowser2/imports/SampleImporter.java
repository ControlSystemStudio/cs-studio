/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.io.InputStream;
import java.util.List;

import org.diirt.vtype.VType;

/** API for tool that imports data
 *
 *  @author Kay Kasemir
 */
public interface SampleImporter
{
    /** Perform value import
     *
     *  <p>Implementers should note that the same imported instance
     *  can be called in parallel for multiple files,
     *  so this method should be re-entrant.
     *
     *  @param input Input stream
     *  @return Values
     *  @throws Exception on error
     */
    public List<VType> importValues(final InputStream input) throws Exception;
}
