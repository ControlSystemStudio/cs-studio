package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderException;

import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author Kunal Shroff
 *
 */
public class GetAllProperties implements Callable<Collection<String>> {
    private List<ExceptionListener> listeners = new CopyOnWriteArrayList<ExceptionListener>();

    public void addExceptionListener(ExceptionListener listener) {
        this.listeners.add(listener);
    }

    @SuppressWarnings("unused")
    public void removeExceptionListener(ExceptionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public Collection<String> call() throws Exception {
        try {
            return ChannelFinder.getClient().getAllProperties();
        } catch (ChannelFinderException e) {
            for (ExceptionListener listener : this.listeners) {
                listener.exceptionThrown(e);
            }
            return null;
        }
    }

}