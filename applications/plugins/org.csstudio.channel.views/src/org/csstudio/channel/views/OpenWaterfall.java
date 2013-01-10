package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

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
	protected void execute(List<ProcessVariable> pvs, ExecutionEvent event)
			throws Exception {
		if (!pvs.isEmpty()) {
			findView(WaterfallView.class, WaterfallView.ID)
				.setChannelQuery(ChannelQuery.query(pvs.get(0).getName()).build());
		}
	}

}
