/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.model;

/** A control system object should provide a list of serializable types
 *  to support data exchange via Drag-and-Drop.
 *
 *  The control system object doesn't have to implement this interface directly.
 *  The only requirement is that it is <code>Serializable</code>.
 *
 *  A control system object can provide data in several ways.
 *  For example, an application model item might be a magnet, i.e. a 'Device' with
 *  several PV names. It can be transferred via Drag-and-Drop as the original Magnet,
 *  but also as a Device or as an array of PV names.
 *
 *  By adapting to this interface, the control system object can expose itself
 *  as { Magnet.class, Device.class, ProcessVariableName[].class }
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
public interface ControlSystemObject
{
    // TODO should this return something like Class<? extends Serializable>[]
    /** @return Array of types as which this control system object can be serialized */
    @SuppressWarnings("rawtypes")
    public Class[] getSerializableTypes();
}
