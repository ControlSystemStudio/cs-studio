package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides the basic infrastructure for a widget that works with a channel query.
 * Handles the basics for async communication.
 * 
 * @author carcassi
 */
public abstract class AbstractChannelWidget extends Composite {
	
	private ChannelQuery channelQuery;
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public AbstractChannelWidget(Composite parent, int style) {
		super(parent, style);
	}
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public ChannelQuery getChannelQuery() {
		return channelQuery;
	}
	
	public void setChannelQuery(ChannelQuery channelQuery) {
		ChannelQuery oldValue = this.channelQuery;
		this.channelQuery = channelQuery;
		if (oldValue != null) {
			oldValue.removeChannelQueryListener(queryListener);
		}
		queryCleared();
		if (channelQuery != null) {
			channelQuery.execute(queryListener);
		}
		changeSupport.firePropertyChange("channelQuery", oldValue, channelQuery);
	}
	
	private final ChannelQueryListener queryListener = new ChannelQueryListener() {
		
		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				
				@Override
				public void run() {
					AbstractChannelWidget.this.queryExecuted(result);
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
