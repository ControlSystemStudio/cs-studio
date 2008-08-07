package org.csstudio.config.kryonamebrowser.ui.provider;

import java.sql.SQLException;
import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KryoNameContentProvider implements IStructuredContentProvider {

	private KryoNameBrowserLogic logic;

	public KryoNameContentProvider(KryoNameBrowserLogic logic) {
		this.logic = logic;
	}

	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof KryoNameEntry) {
			KryoNameEntry example = (KryoNameEntry) inputElement;

			try {
				List<KryoNameResolved> search = logic.search(example);
				return search.toArray();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return new Object[] {};
	}

	@Override
	public void dispose() {
		// we can ignore this

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// we can ignore this

	}

}
