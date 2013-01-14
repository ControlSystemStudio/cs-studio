package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Composite;

/**
 * Provides the basic infrastructure for a widget that works with a channel query.
 * 
 * @author carcassi
 */
public abstract class AbstractChannelQueryWidget extends Composite {
	
	private ChannelQuery channelQuery;
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public AbstractChannelQueryWidget(Composite parent, int style) {
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
		// If new query is the same, don't change -- you may lose the cached result
		if (getChannelQuery() != null && getChannelQuery().equals(channelQuery))
			return;
		if (getChannelQuery() == null && channelQuery == null)
			return;
		
		ChannelQuery oldValue = this.channelQuery;
		this.channelQuery = channelQuery;
		changeSupport.firePropertyChange("channelQuery", oldValue, channelQuery);
	}
	
}
