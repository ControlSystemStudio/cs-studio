/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

/**
 * A collector that keeps only the latest value.
 *
 * @author carcassi
 */
public class LatestValueCollector<T> implements Collector<T, T> {
    
    private T value;

    @Override
    public void setValue(T newValue) {
        value = newValue;
    }

    @Override
    public T getValue() {
        return value;
    }
    
}
