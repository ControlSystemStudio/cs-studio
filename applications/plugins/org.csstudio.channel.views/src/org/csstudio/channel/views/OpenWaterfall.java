package org.csstudio.channel.views;

import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 * Opens the waterfall view.
 * 
 * @author carcassi
 */
public class OpenWaterfall extends AbstractAdaptedHandler<ProcessVariable> {
	
	public OpenWaterfall() {
		super(ProcessVariable.class);
	}

	@Override
	protected void execute(List<ProcessVariable> queries, ExecutionEvent event)
			throws Exception {
		if (!queries.isEmpty()) {
			findView(WaterfallView.class, WaterfallView.ID)
				.setPVName(queries.get(0).getName());
		}
	}

}
