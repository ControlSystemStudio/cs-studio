/**
 * 
 */
package org.epics.pvmanager.graphene;

import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 * @author shroffk
 *
 */
public class ScatterGraphPlot extends DesiredRateExpressionImpl<Plot2DResult> {

    public ScatterGraphPlot(DesiredRateExpressionList<?> childExpressions,
	    ReadFunction<Plot2DResult> function, String defaultName) {
	super(childExpressions, function, defaultName);
    }
    
    public void update(ScatterGraph2DRendererUpdate update) {
        ((ScatterGraphFunction) getFunction()).getRendererUpdateQueue().writeValue(update);
    }

}
