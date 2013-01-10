package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides the basic infrastructure for a widget that works with a channel query result.
 * Handles the basics for async communication.
 * 
 * @author carcassi
 */
public abstract class AbstractChannelQueryResultWidget extends AbstractChannelQueryWidget {

	public AbstractChannelQueryResultWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	public void setChannelQuery(ChannelQuery channelQuery) {
		// If new query is the same, don't change -- you would re-trigger the query for nothing
		if (getChannelQuery() != null && getChannelQuery().equals(channelQuery))
			return;
		
		ChannelQuery oldValue = getChannelQuery();
		if (oldValue != null) {
			oldValue.removeChannelQueryListener(queryListener);
		}
		queryCleared();
		if (channelQuery != null) {
			channelQuery.execute(queryListener);
		}
		super.setChannelQuery(channelQuery);
	}
	
	private final ChannelQueryListener queryListener = new ChannelQueryListener() {
		
		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				
				@Override
				public void run() {
					AbstractChannelQueryResultWidget.this.queryExecuted(result);
				}
			});
			
		}
	};
	
	/**
	 * This method should clear all the displayed information about the query as
	 * the query is either non-existent or not yet finished. This is called on the
	 * SWT thread.
	 */
	protected abstract void queryCleared();
	
	/**
	 * This method should display the given result returned by the query execution.
	 * This is called on the SWT thread.
	 * 
	 * @param result the result of the query
	 */
	protected abstract void queryExecuted(Result result);
	
}
