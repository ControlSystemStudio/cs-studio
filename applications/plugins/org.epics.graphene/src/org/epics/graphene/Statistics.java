/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public interface Statistics {
    public int getCount();
    public Number getMinimum();
    public Number getMaximum();
    public double getAverage();
    public double getStdDev();
}
