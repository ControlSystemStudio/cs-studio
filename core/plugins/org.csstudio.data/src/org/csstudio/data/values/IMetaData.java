/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

import java.io.Serializable;

/** Base interface for a sample's meta data.
 *  @see IValue
 *  @see INumericMetaData
 *  @see IEnumeratedMetaData
 *  @author Kay Kasemir
 */
public interface IMetaData extends Serializable
{
    // No content, only defined as base for other *MetaData interfaces
}
