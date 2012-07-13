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

import org.csstudio.data.values.IValue;

/** API for tool that imports data
 *
 *  @author Kay Kasemir
 */
public interface SampleImporter
{
    public List<IValue> importValues(final InputStream input) throws Exception;
}
