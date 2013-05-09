/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;

/**
 * @author shroffk
 *
 */
public class ScatterGraph2DExpression extends DesiredRateExpressionImpl<Graph2DResult> implements Graph2DExpression<ScatterGraph2DRendererUpdate> {

    public ScatterGraph2DExpression(DesiredRateExpressionList<?> childExpressions,
            ReadFunction<Graph2DResult> function, String defaultName) {
        super(childExpressions, function, defaultName);
    }

    public ScatterGraph2DExpression(DesiredRateExpression<? extends VTable> tableData,
	    DesiredRateExpression<? extends VString> xColumnName,
	    DesiredRateExpression<? extends VString> yColumnName,
	    DesiredRateExpression<? extends VString> tooltipColumnName) {
        super(new DesiredRateExpressionListImpl<>().and(tableData)
                .and(xColumnName).and(yColumnName).and(tooltipColumnName),
                new ScatterGraph2DTableFunction(tableData.getFunction(),
                xColumnName.getFunction(), yColumnName.getFunction(), tooltipColumnName.getFunction()),
                "Scatter Graph");
    }

    @Override
    public void update(ScatterGraph2DRendererUpdate update) {
        ((ScatterGraph2DFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

    @Override
    public ScatterGraph2DRendererUpdate newUpdate() {
        return new ScatterGraph2DRendererUpdate();
    }
}
