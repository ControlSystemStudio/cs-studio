/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

/** Descriptor for the position of an element
 *  in the alarm tree hierarchy.
 *  <p>
 *  In some cases (Root, PV) this directly maps
 *  to the sub-type of AlarmTree that's used to
 *  hold the item data, but Area, Component, SubComponent
 *  all use AlarmTreeComponent because they are otherwise
 *  functioning in the same way.
 *  @author Kay Kasemir
 */
public enum AlarmTreePosition
{
    /** Root of the alarm tree, held in an AlarmTreeRoot */
    Root,

    /** Top-level alarm tree element, meant to describe an area or facility.
     *  Held in an AlarmTreeItem
     */
    Area,

    /** Alarm tree element just below an Area, meant to describe a system
     *  or component.
     *  Held in an AlarmTreeItem
     */
    System,

    /** Alarm tree PV.
     *  Held in an AlarmTreePV
     */
    PV
}
