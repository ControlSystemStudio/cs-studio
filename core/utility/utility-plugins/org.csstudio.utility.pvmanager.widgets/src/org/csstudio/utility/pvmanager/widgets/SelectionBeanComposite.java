package org.csstudio.utility.pvmanager.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

/**
 * Widget that can display a formula that returns a VTable.
 * 
 * @author carcassi
 */
public abstract class SelectionBeanComposite extends BeanComposite implements ISelectionProvider {

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public SelectionBeanComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	
	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(createSelection());
	}
	
	protected abstract Object createSelection();

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	private void fireSelectionChangedListener() {
		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, getSelection()));
		}
	}
	
	protected void forwardPropertyChangeToSelection(String propertyName) {
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				fireSelectionChangedListener();
			}
		});
	}

}
