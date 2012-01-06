package org.csstudio.channel.widgets;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Helper class to wrap a selection provider (e.g. a table, a tree, ...),
 * so that events are fired at the same time, but the selection is changed
 * to a different type (e.g. String to ProcessVariable).
 * 
 * @author carcassi
 */
public abstract class AbstractSelectionProviderWrapper implements ISelectionProvider {

	private final ISelectionProvider wrappedProvider;
	private final ISelectionProvider eventSource;
	
	public AbstractSelectionProviderWrapper(ISelectionProvider wrappedProvider,
			ISelectionProvider eventSource) {
		this.wrappedProvider = wrappedProvider;
		this.eventSource = eventSource;
	}
	
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		wrappedProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				listener.selectionChanged(new SelectionChangedEvent(eventSource, getSelection()));
			}
			
			@Override
			public int hashCode() {
				return listener.hashCode();
			}
			
			@Override
			public boolean equals(Object obj) {
				return listener.equals(obj);
			}
		});
	}


	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		wrappedProvider.removeSelectionChangedListener(listener);
	}
	
	@Override
	public ISelection getSelection() {
		return transform(wrappedProvider.getSelection());
	}

	@Override
	public void setSelection(ISelection selection) {
		wrappedProvider.setSelection(reverseTransform(selection));
	}
	
	protected abstract ISelection transform(ISelection selection);
	
	protected ISelection reverseTransform(ISelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
