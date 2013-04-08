/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.Graph2DRendererUpdate;
import org.epics.pvmanager.expression.DesiredRateExpression;

/**
 *
 * @author carcassi
 */
public interface Graph2DExpression<T extends Graph2DRendererUpdate<T>> extends DesiredRateExpression<Graph2DResult> {
    
    public T newUpdate();
    
    public void update(T update);
    
}
