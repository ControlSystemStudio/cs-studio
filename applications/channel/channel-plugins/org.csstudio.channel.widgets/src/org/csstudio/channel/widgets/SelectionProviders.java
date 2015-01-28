package org.csstudio.channel.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SelectionProviders {
	
	/**
	 * Returns a selection provide that returns the data out of all
	 * the treeItem selected.
	 * 
	 * @param tree tree on which the selection provider is based
	 * @return a new selection provider
	 */
	public static ISelectionProvider treeItemDataSelectionProvider(
			final Tree tree) {
		return new ISelectionProvider() {

			private Map<ISelectionChangedListener, SelectionAdapter> map = new HashMap<ISelectionChangedListener, SelectionAdapter>();

			@Override
			public void setSelection(ISelection selection) {
				throw new UnsupportedOperationException("Not implemented");
			}

			@Override
			public void removeSelectionChangedListener(
					ISelectionChangedListener listener) {
				SelectionAdapter adapter = map.remove(listener);
				if (adapter != null)
					tree.removeSelectionListener(adapter);
			}

			@Override
			public ISelection getSelection() {
				TreeItem[] selection = tree.getSelection();
				Object[] data = new Object[selection.length];
				for (int i = 0; i < data.length; i++) {
					data[i] = selection[i].getData();
				}
				return new StructuredSelection(data);
			}

			@Override
			public void addSelectionChangedListener(
					final ISelectionChangedListener listener) {
				final ISelectionProvider thisProvider = this;
				SelectionAdapter adapter = new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						listener.selectionChanged(new SelectionChangedEvent(
								thisProvider, getSelection()));
					}

				};
				map.put(listener, adapter);
				tree.addSelectionListener(adapter);
			}
		};

	}
}
