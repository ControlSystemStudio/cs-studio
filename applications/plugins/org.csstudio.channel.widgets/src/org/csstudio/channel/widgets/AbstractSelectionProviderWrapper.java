package org.csstudio.channel.widgets;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

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
				// TODO: this doesn't actually work!
				return listener.equals(obj);
			}
		});
	}


	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO: this doesn't actually work!
		wrappedProvider.removeSelectionChangedListener(listener);
	}
	
	@Override
	public ISelection getSelection() {
		ISelection selection = wrappedProvider.getSelection();
		if (selection instanceof IStructuredSelection) {
			return transform((IStructuredSelection) wrappedProvider.getSelection());
		} else {
			return new StructuredSelection();
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			wrappedProvider.setSelection(reverseTransform((IStructuredSelection) selection));
		} else {
			wrappedProvider.setSelection(new StructuredSelection());
		}
	}
	
	protected abstract ISelection transform(IStructuredSelection selection);
	
	protected ISelection reverseTransform(IStructuredSelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
