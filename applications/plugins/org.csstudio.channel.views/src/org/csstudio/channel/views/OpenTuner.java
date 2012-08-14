package org.csstudio.channel.views;

import java.util.List;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.channel.widgets.TunerWidget;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.PartInitException;

public class OpenTuner extends AbstractAdaptedHandler<ChannelQuery> {

	public OpenTuner() {
		super(ChannelQuery.class);
	}

	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event)
			throws PartInitException {
		if (!queries.isEmpty()) {
			findView(TunerView.class, TunerView.ID).setChannelQuery(
					queries.get(0));
		}
	}

}
