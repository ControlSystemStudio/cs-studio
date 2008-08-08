package org.csstudio.config.kryonamebrowser.ui.provider;

import java.sql.SQLException;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KryoObjectContentComboProvider implements IStructuredContentProvider {

	private KryoNameBrowserLogic logic;
	
	public void setLogic(KryoNameBrowserLogic logic) {
		this.logic = logic;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		try {
			return logic.findObjectChoices((KryoObjectEntry) inputElement).toArray();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

}
